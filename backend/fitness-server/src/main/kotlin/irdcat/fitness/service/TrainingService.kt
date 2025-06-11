package irdcat.fitness.service

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import irdcat.fitness.exception.TrainingNotFoundException
import irdcat.fitness.repository.TrainingRepository
import irdcat.fitness.service.dto.TrainingDto
import irdcat.fitness.service.model.TrainingExercise
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuples
import java.time.LocalDate
import java.util.UUID

@Service
class TrainingService(
    private val trainingRepository: TrainingRepository
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
    }

    fun findPagedTrainingsBetweenDates(
        from: LocalDate,
        to: LocalDate,
        page: Long,
        pageSize: Long
    ): Mono<Page<TrainingDto>> {

        val trainings = trainingRepository.findBetweenDates(from, to, page * pageSize, pageSize);
        val count = trainingRepository.countBetweenDates(from, to)

        return trainings
            .collectList()
            .zipWith(count)
            .map { Page(it.t1, page, pageSize, it.t2.value) }
            .switchIfEmpty(Page(listOf<TrainingDto>(), page, pageSize, 0).toMono())
            .doOnNext {
                logger.debug("Training Page: [page={}, size={}, total={}]",
                    it.currentPage, it.pageSize, it.totalResults)
            }
    }

    fun findByDate(date: LocalDate): Mono<TrainingDto> {

        return trainingRepository
            .findByDate(date)
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
            .flatMapMany { trainingRepository.insertExercises(it) }
            .doOnNext { logger.debug("Added: {}", it) }
    }

    private fun updateRequestedExercises(trainingDto: TrainingDto): Flux<TrainingExercise> {

        val existingTrainingExerciseIds = trainingDto.toMono()
            .flatMap { trainingRepository.findByDate(it.date) }
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany(Flux<TrainingExercise>::fromIterable)
            .map(TrainingExercise::id)
            .collectList()

        val updatedTrainingExerciseIds = trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { Flux<TrainingExercise>.fromIterable(it) }
            .map(TrainingExercise::id)
            .collectList()

        val intersection = updatedTrainingExerciseIds
            .zipWith(existingTrainingExerciseIds)
            .map { it.t1.intersect(it.t2) }

        return intersection
            .doOnNext { logger.debug("Attempting to update {} exercises", it.size) }
            .zipWith(trainingDto.toMono())
            .map { Tuples.of(it.t1, it.t2.toTrainingExercises()) }
            .map { it.t2.filter { te -> it.t1.contains(te.id) } }
            .flatMapMany { trainingRepository.updateExercises(it) }
            .doOnNext { logger.debug("Updated: {} ", it) }
    }

    private fun deleteRequestedExercises(trainingDto: TrainingDto): Mono<Void> {

        val existingTrainingExerciseIds = trainingDto.toMono()
            .flatMap { trainingRepository.findByDate(it.date) }
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany(Flux<TrainingExercise>::fromIterable)
            .map(TrainingExercise::id)
            .collectList()

        val updatedTrainingExerciseIds = trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { Flux<TrainingExercise>.fromIterable(it) }
            .map(TrainingExercise::id)
            .collectList()

        val subtraction = updatedTrainingExerciseIds
            .zipWith(existingTrainingExerciseIds)
            .map { it.t2.subtract(it.t1).toList().map { it!! } }

        return subtraction
            .doOnNext { logger.debug("Attempting to delete {} exercises.", it.size) }
            .flatMapMany { trainingRepository.deleteExercises(it) }
            .then()
    }

    fun deleteByDate(date: LocalDate): Mono<Void> {
        return trainingRepository.deleteByDate(date)
    }

    fun exportToYaml(): Mono<Resource> {

        return trainingRepository
            .findAll()
            .toResourceMono(yamlMapper)
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun exportToJson(): Mono<Resource> {

        return trainingRepository
            .findAll()
            .toResourceMono(jsonMapper)
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromYaml(file: FilePart): Mono<Void> {

        return file
            .toMappedFlux(yamlMapper, TrainingDto::class.java)
            .import()
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromJson(file: FilePart): Mono<Void> {

        return file
            .toMappedFlux(jsonMapper, TrainingDto::class.java)
            .import()
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun Flux<TrainingDto>.import(): Mono<Void> {
        return flatMapIterable(TrainingDto::toTrainingExercises)
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .flatMapMany { trainingRepository.insertExercises(it) }
            .doOnNext { logger.debug("Imported training exercise: {}", it) }
            .then()
    }
}
