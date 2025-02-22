package irdcat.fitness.service

import com.mongodb.BasicDBObject
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.Date

@Service
class TrainingService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        const val GROUP_KEY = "_id"
        const val DATE = "date"
        const val BODYWEIGHT = "bodyweight"
        const val EXERCISE = "exercise"
        const val EXERCISES = "exercises"
        const val SETS = "sets"
        const val COUNT = "count"
    }

    fun findTrainingsBetweenDates(from: Date, to: Date, page: Long, pageSize: Long): Mono<Page<TrainingDto>> {

        val dateCriteria = Criteria.where(DATE).gte(from).lte(to)
        val matchOperation = match(dateCriteria)
        val groupOperation = group(DATE)
            .first(BODYWEIGHT).`as`(BODYWEIGHT)
            .push(
                BasicDBObject()
                    .append(GROUP_KEY, "\$$GROUP_KEY")
                    .append(EXERCISE, "\$$EXERCISE")
                    .append(SETS, "\$$SETS")
            ).`as`(EXERCISES)
        val projectionOperation = project()
            .and(GROUP_KEY).`as`(DATE)
            .and(BODYWEIGHT).`as`(BODYWEIGHT)
            .and(EXERCISES).`as`(EXERCISES)
        val sortOperation = sort(Direction.DESC, DATE)
        val skipOperation = skip((page * pageSize).toLong())
        val limitOperation = limit(pageSize.toLong())
        val groupCountingOperation = group()
            .count().`as`(COUNT)

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            projectionOperation,
            sortOperation,
            skipOperation,
            limitOperation)

        val countingAggregation = newAggregation(
            matchOperation,
            groupOperation,
            groupCountingOperation)

        data class CountIntermediate(val count: Long)

        val resultsMono = reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
            .collectList()
        val countMono = reactiveMongoTemplate
            .aggregate(countingAggregation, TrainingExercise::class.java, CountIntermediate::class.java)
            .next()

        logger.debug("Find between dates aggregation: {}", aggregation)
        logger.debug("Find between dates counting aggregation: {}", countingAggregation)
        return resultsMono
            .zipWith(countMono)
            .map { Page(it.t1, page, pageSize, it.t2.count) }
            .doOnNext { logger.debug("Training Page: [page={}, size={}, total={}]", it.currentPage, it.pageSize, it.totalResults) }
    }

    fun findByDate(date: Date): Mono<TrainingDto> {

        val matchOperation = match(Criteria.where(DATE).`is`(date))
        val groupOperation = group(DATE)
            .first(BODYWEIGHT).`as`(BODYWEIGHT)
            .push(
                BasicDBObject()
                    .append(GROUP_KEY, "\$$GROUP_KEY")
                    .append(EXERCISE, "\$$EXERCISE")
                    .append(SETS, "\$$SETS")
            ).`as`(EXERCISES)
        val limitOperation = limit(1)
        val projectionOperation = project()
            .and(GROUP_KEY).`as`(DATE)
            .and(BODYWEIGHT).`as`(BODYWEIGHT)
            .and(EXERCISES).`as`(EXERCISES)

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
    }

    fun createOrUpdate(trainingDto: TrainingDto): Mono<TrainingDto> {

        return trainingDto.toMono()
            .map(TrainingDto::toTrainingExercises)
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingExercise::class.java) }
            .doOnNext { logger.debug("Training Exercise: {}", it) }
            .collectList()
            .map(TrainingDto::fromTrainingExercises)
    }
}