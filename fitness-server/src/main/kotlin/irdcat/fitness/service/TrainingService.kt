package irdcat.fitness.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.StreamWriteFeature
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mongodb.BasicDBObject
import irdcat.fitness.exception.TrainingNotFoundException
import kotlinx.coroutines.reactive.collect
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuples
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Collectors

@Service
class TrainingService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        // WRITE_DOC_START_MARKER has to be disabled at factory level
        // https://github.com/FasterXML/jackson-dataformats-text/issues/215
        private val yamlFactory = YAMLFactory.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .build()
        private val jsonFactory = JsonFactory.builder()
            .build()
        private val yamlMapper = YAMLMapper(yamlFactory)
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
        private val jsonMapper = JsonMapper(jsonFactory)
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)

        const val GROUP_KEY = "_id"
        const val ID = "_id"
        const val DATE = "date"
        const val BODYWEIGHT = "bodyweight"
        const val EXERCISE = "exercise"
        const val EXERCISES = "exercises"
        const val SETS = "sets"
        const val COUNT = "count"
        const val ORDER = "order"
    }

    fun findPagedTrainingsBetweenDates(
        from: LocalDate,
        to: LocalDate,
        page: Long,
        pageSize: Long
    ): Mono<Page<TrainingDto>> {

        val trainings = findTrainingsBetweenDates(from, to, (page*pageSize).toLong(), pageSize.toLong())
        val count = countTrainingsBetweenDates(from, to)

        return trainings
            .collectList()
            .zipWith(count)
            .map { Page(it.t1, page, pageSize, it.t2) }
            .switchIfEmpty(Page(listOf<TrainingDto>(), page, pageSize, 0).toMono())
            .doOnNext {
                logger.debug("Training Page: [page={}, size={}, total={}]",
                    it.currentPage, it.pageSize, it.totalResults)
            }
    }

    private fun findTrainingsBetweenDates(
        from: LocalDate,
        to: LocalDate,
        skip: Long,
        limit: Long
    ): Flux<TrainingDto> {

        val matchOperation = matchBetweenDates(from, to)
        val sortByOrderOperation = sort(Direction.ASC, ORDER)
        val groupOperation = groupByDate()
        val projectionOperation = projectGroupedByDate()
        val sortOperation = sort(Direction.DESC, DATE)
        val skipOperation = skip(skip)
        val limitOperation = limit(limit)

        val aggregation = newAggregation(
            matchOperation,
            sortByOrderOperation,
            groupOperation,
            projectionOperation,
            sortOperation,
            skipOperation,
            limitOperation)

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
    }

    private fun countTrainingsBetweenDates(
        from: LocalDate,
        to: LocalDate
    ): Mono<Long> {

        val matchOperation = matchBetweenDates(from, to)
        val groupOperation = groupByDate()
        val groupCountingOperation = group()
            .count().`as`(COUNT)

        val countingAggregation = newAggregation(
            matchOperation,
            groupOperation,
            groupCountingOperation)

        data class CountIntermediate(val count: Long)

        return reactiveMongoTemplate
            .aggregate(countingAggregation, TrainingExercise::class.java, CountIntermediate::class.java)
            .next()
            .map { it.count }
    }

    fun findByDate(date: LocalDate): Mono<TrainingDto> {

        val matchOperation = matchByDate(date)
        val groupOperation = groupByDate()
        val limitOperation = limit(1)
        val projectionOperation = projectGroupedByDate()

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            limitOperation,
            projectionOperation)

        logger.debug("Find by date aggregation: {}", aggregation)
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
            .next()
            .doOnNext { logger.debug("TrainingDto: {}", it) }
            .switchIfEmpty(TrainingNotFoundException("Training at $date not found").toMono())
    }

    fun createOrUpdate(trainingDto: TrainingDto): Mono<TrainingDto> {

        val deleted = deleteRequestedExercises(trainingDto)
        val updated = updateRequestedExercises(trainingDto)
        val added = addRequestedExercises(trainingDto)

        return deleted
            .thenMany(updated)
            .mergeWith(added)
            .collectList()
            .map(TrainingDto::fromTrainingExercises)
    }

    private fun addRequestedExercises(trainingDto: TrainingDto): Flux<TrainingExercise> {

        return trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { Flux.fromIterable(it) }
            .filter { it.id.isNullOrEmpty() }
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .doOnNext { logger.debug("Attempting to add {} exercises.", it.size) }
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Added: {}", it) }
    }

    private fun updateRequestedExercises(trainingDto: TrainingDto): Flux<TrainingExercise> {

        val existingExerciseIds = trainingDto.toMono()
            .map { Criteria.where(DATE).isEqualTo(it.date) }
            .map { Query(it) }
            .flatMapMany { reactiveMongoTemplate.find(it, TrainingExercise::class.java) }
            .map(TrainingExercise::id)
            .collectList()

        return trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { Flux.fromIterable(it) }
            .map(TrainingExercise::id)
            .collectList()
            .zipWith(existingExerciseIds)
            .map { it.t1.intersect(it.t2) }
            .doOnNext { logger.debug("Attempting to update {} exercises", it.size) }
            .zipWith(trainingDto.toMono())
            .map { Tuples.of(it.t1, it.t2.toTrainingExercises()) }
            .map { it.t2.filter { te -> it.t1.contains(te.id) } }
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { reactiveMongoTemplate.save(it) }
            .doOnNext { logger.debug("Updated: {} ", it) }
    }

    private fun deleteRequestedExercises(trainingDto: TrainingDto): Mono<Void> {

        val existingExerciseIds = trainingDto.toMono()
            .map { Criteria.where(DATE).isEqualTo(it.date) }
            .map { Query(it) }
            .flatMapMany { reactiveMongoTemplate.find(it, TrainingExercise::class.java) }
            .map(TrainingExercise::id)
            .collectList()

        return trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { Flux.fromIterable(it) }
            .map(TrainingExercise::id)
            .collectList()
            .zipWith(existingExerciseIds)
            .map { it.t2.subtract(it.t1) }
            .doOnNext { logger.debug("Attempting to delete {} exercises.", it.size) }
            .map { Criteria.where(ID).`in`(it) }
            .map { Query().addCriteria(it) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Deleted {} exercises.", it.deletedCount) }
            .then()
    }

    fun deleteByDate(date: LocalDate): Mono<Void> {

        return date.toMono()
            .map { findByDateCriteria(date) }
            .map { Query().addCriteria(it) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Deleted {} exercises associated with {}.", it.deletedCount, date) }
            .then()
    }

    fun exportToYaml(): Mono<Resource> {

        return getAllTrainings()
            .collectList()
            .map {
                val byteArrayOutputStream = ByteArrayOutputStream()
                yamlMapper.writeValue(byteArrayOutputStream, it)
                byteArrayOutputStream.flush()
                val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                InputStreamResource(byteArrayInputStream) as Resource
            }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun exportToJson(): Mono<Resource> {

        return getAllTrainings()
            .collectList()
            .map {
                val byteArrayOutputStream = ByteArrayOutputStream()
                jsonMapper.writeValue(byteArrayOutputStream, it)
                byteArrayOutputStream.flush()
                val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                InputStreamResource(byteArrayInputStream) as Resource
            }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromYaml(file: FilePart): Mono<Void> {

        return file.toMono()
            .flatMapMany(FilePart::content)
            .map { it.asInputStream() }
            .toMono()
            .map { yamlMapper.readValue(it, object: TypeReference<List<TrainingDto>>(){}) }
            .flatMapMany(Flux<TrainingDto>::fromIterable)
            .flatMapIterable(TrainingDto::toTrainingExercises)
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Imported training exercise: {}", it) }
            .then()
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromJson(file: FilePart): Mono<Void> {

        return file.toMono()
            .flatMapMany(FilePart::content)
            .map { it.asInputStream() }
            .toMono()
            .map { jsonMapper.readValue(it, object: TypeReference<List<TrainingDto>>(){}) }
            .flatMapMany(Flux<TrainingDto>::fromIterable)
            .flatMapIterable(TrainingDto::toTrainingExercises)
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Imported training exercise: {}", it) }
            .then()
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun getAllTrainings(): Flux<TrainingDto> {

        val sortByOrderOperation = sort(Direction.ASC, ORDER)
        val groupOperation = groupByDate()
        val projectionOperation = projectGroupedByDate()
        val sortOperation = sort(Direction.DESC, DATE)

        val aggregation = newAggregation(
            sortByOrderOperation,
            groupOperation,
            projectionOperation,
            sortOperation)

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
    }

    private fun matchBetweenDates(from: LocalDate, to: LocalDate): MatchOperation {

        val dateCriteria = Criteria.where(DATE).gte(from).lte(to)
        return match(dateCriteria)
    }

    private fun matchByDate(date: LocalDate): MatchOperation {

        val dateCriteria = findByDateCriteria(date)
        return match(dateCriteria)
    }

    private fun groupByDate(): GroupOperation {

        return group(DATE)
            .first(BODYWEIGHT).`as`(BODYWEIGHT)
            .push(
                BasicDBObject()
                    .append(GROUP_KEY, "\$$GROUP_KEY")
                    .append(EXERCISE, "\$$EXERCISE")
                    .append(ORDER, "\$$ORDER")
                    .append(SETS, "\$$SETS")
            ).`as`(EXERCISES)
    }

    private fun projectGroupedByDate(): ProjectionOperation {

        return project()
            .and(GROUP_KEY).`as`(DATE)
            .and(ORDER).`as`(ORDER)
            .and(BODYWEIGHT).`as`(BODYWEIGHT)
            .and(EXERCISES).`as`(EXERCISES)
    }

    private fun findByDateCriteria(date: LocalDate): Criteria {

        return Criteria.where(DATE).isEqualTo(date)
    }
}
