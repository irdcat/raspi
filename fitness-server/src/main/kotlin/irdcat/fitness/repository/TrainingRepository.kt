package irdcat.fitness.repository

import com.mongodb.BasicDBObject
import irdcat.fitness.repository.model.ValueHolder
import irdcat.fitness.service.dto.TrainingDto
import irdcat.fitness.service.model.TrainingExercise
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@Repository
class TrainingRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    
    companion object {
        const val GROUP_KEY = "_id"
        const val ID = "_id"
        const val VALUE = "value"
        const val DATE = "date"
        const val BODYWEIGHT = "bodyweight"
        const val EXERCISE = "exercise"
        const val EXERCISES = "exercises"
        const val SETS = "sets"
        const val ORDER = "order"
    }

    fun findBetweenDates(from: LocalDate, to: LocalDate, skip: Long, limit: Long): Flux<TrainingDto> {
        val aggregation = newAggregation(
            match(where(DATE).gte(from).lte(to)),
            sort(Direction.ASC, ORDER),
            group(DATE)
                .first(BODYWEIGHT).`as`(BODYWEIGHT)
                .push(
                    BasicDBObject()
                        .append(GROUP_KEY, "\$${GROUP_KEY}")
                        .append(EXERCISE, "\$${EXERCISE}")
                        .append(ORDER, "\$${ORDER}")
                        .append(SETS, "\$${SETS}")
                ).`as`(EXERCISES),
            project()
                .and(GROUP_KEY).`as`(DATE)
                .and(ORDER).`as`(ORDER)
                .and(BODYWEIGHT).`as`(BODYWEIGHT)
                .and(EXERCISES).`as`(EXERCISES),
            sort(Direction.DESC, DATE),
            skip(skip),
            limit(limit))

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
    }

    fun countBetweenDates(from: LocalDate, to: LocalDate): Mono<ValueHolder<Long>> {
        val aggregation = newAggregation(
            match(where(DATE).gte(from).lte(to)),
            group(DATE)
                .first(BODYWEIGHT).`as`(BODYWEIGHT)
                .push(
                    BasicDBObject()
                        .append(GROUP_KEY, "\$${GROUP_KEY}")
                        .append(EXERCISE, "\$${EXERCISE}")
                        .append(ORDER, "\$${ORDER}")
                        .append(SETS, "\$${SETS}")
                ).`as`(EXERCISES),
            group()
                .count().`as`(VALUE))

        val valueHolderClass = ValueHolder<Long>(0L).javaClass
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, valueHolderClass)
            .next()
    }

    fun findAll(): Flux<TrainingDto> {
        val aggregation = newAggregation(
            sort(Direction.ASC, ORDER),
            group(DATE)
                .first(BODYWEIGHT).`as`(BODYWEIGHT)
                .push(
                    BasicDBObject()
                        .append(GROUP_KEY, "\$$GROUP_KEY")
                        .append(EXERCISE, "\$$EXERCISE")
                        .append(ORDER, "\$$ORDER")
                        .append(SETS, "\$$SETS")
                ).`as`(EXERCISES),
            project()
                .and(GROUP_KEY).`as`(DATE)
                .and(ORDER).`as`(ORDER)
                .and(BODYWEIGHT).`as`(BODYWEIGHT)
                .and(EXERCISES).`as`(EXERCISES),
            sort(Direction.DESC, DATE)
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
    }

    fun findByDate(date: LocalDate): Mono<TrainingDto> {
        val aggregation = newAggregation(
            match(where(DATE).isEqualTo(date)),
            group(DATE)
                .first(BODYWEIGHT).`as`(BODYWEIGHT)
                .push(
                    BasicDBObject()
                        .append(GROUP_KEY, "\$$GROUP_KEY")
                        .append(EXERCISE, "\$$EXERCISE")
                        .append(ORDER, "\$$ORDER")
                        .append(SETS, "\$$SETS")
                ).`as`(EXERCISES),
            limit(1),
            project()
                .and(GROUP_KEY).`as`(DATE)
                .and(ORDER).`as`(ORDER)
                .and(BODYWEIGHT).`as`(BODYWEIGHT)
                .and(EXERCISES).`as`(EXERCISES)
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
            .next()
    }

    fun deleteByDate(date: LocalDate): Mono<Void> {
        return date.toMono()
            .map { query(where(DATE).isEqualTo(date)) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingExercise::class.java) }
            .then()
    }

    fun insertExercises(exercises: List<TrainingExercise>): Flux<TrainingExercise> {
        return reactiveMongoTemplate.insert(exercises, TrainingExercise::class.java)
    }

    fun updateExercises(exercises: List<TrainingExercise>): Flux<TrainingExercise> {
        return exercises.toFlux()
            .flatMap{ reactiveMongoTemplate.save(it) }
    }

    fun deleteExercises(ids: List<String>): Mono<Void> {
        return ids.toMono()
            .map { query(where(ID).`in`(it)) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingExercise::class.java) }
            .then()
    }
}