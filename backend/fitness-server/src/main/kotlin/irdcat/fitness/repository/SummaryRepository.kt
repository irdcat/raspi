package irdcat.fitness.repository

import com.mongodb.BasicDBObject
import irdcat.fitness.repository.model.BodyweightParameter
import irdcat.fitness.repository.model.ExerciseSummary
import irdcat.fitness.service.model.TrainingExercise
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.addFields
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply.valueOf
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository
class SummaryRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        const val VALUE = "value"
        const val GROUP_KEY = "_id"
        const val DATA = "data"
        const val DATE = "date"
        const val EXERCISE = "exercise"
        const val EXERCISE_NAME = "exercise.name"
        const val SETS = "sets"
        const val SETS_REPETITIONS = "sets.repetitions"
        const val SETS_WEIGHT = "sets.weight"
        const val SET_VOLUME = "setVolume"
        const val SET_BODYWEIGHT_VOLUME = "setBodyweightVolume"
        const val BODYWEIGHT = "bodyweight"
        const val VOLUME = "volume"
        const val AVERAGE_VOLUME = "averageVolume"
        const val MIN_INTENSITY = "minIntensity"
        const val MAX_INTENSITY = "maxIntensity"
        const val AVG_INTENSITY = "avgIntensity"
        const val BODYWEIGHT_VOLUME = "bodyweightVolume"
        const val AVERAGE_BODYWEIGHT_VOLUME = "averageBodyweightVolume"
        const val PARAMETERS = "parameters"
        const val PARAMETERS_VOLUME = "parameters.volume"
        const val PARAMETERS_AVERAGE_VOLUME = "parameters.averageVolume"
        const val PARAMETERS_MIN_INTENSITY = "parameters.minIntensity"
        const val PARAMETERS_MAX_INTENSITY = "parameters.maxIntensity"
        const val PARAMETERS_AVG_INTENSITY = "parameters.averageIntensity"
        const val PARAMETERS_BODYWEIGHT = "parameters.bodyweight"
        const val PARAMETERS_BODYWEIGHT_VOLUME = "parameters.bodyweightVolume"
        const val PARAMETERS_AVERAGE_BODYWEIGHT_VOLUME = "parameters.averageBodyweightVolume"
    }

    fun findBodyweightsWithDates(from: LocalDate, to: LocalDate): Flux<BodyweightParameter> {
        val aggregation = newAggregation(
            match(where(DATE).gte(from).lte(to)),
            group(DATE)
                .first(BODYWEIGHT).`as`(BODYWEIGHT),
            project()
                .and(GROUP_KEY).`as`(DATE)
                .and(BODYWEIGHT).`as`(VALUE),
            sort(Direction.ASC, DATE))

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, BodyweightParameter::class.java)
    }

    fun findExerciseSummaryParametersWithDates(from: LocalDate, to: LocalDate, name: String): Mono<ExerciseSummary> {
        val aggregation = newAggregation(
            match(
                where(DATE).gte(from).lte(to).and(EXERCISE_NAME).isEqualTo(name)),
            unwind(SETS),
            addFields()
                .addField(SET_VOLUME).withValue(valueOf(SETS_REPETITIONS).multiplyBy(SETS_WEIGHT))
                .addField(SET_BODYWEIGHT_VOLUME).withValue(valueOf(SETS_REPETITIONS).multiplyBy(BODYWEIGHT))
                .build(),
            group(DATE)
                .first(EXERCISE).`as`(EXERCISE)
                .sum(SET_VOLUME).`as`(VOLUME)
                .avg(SET_VOLUME).`as`(AVERAGE_VOLUME)
                .min(SETS_WEIGHT).`as`(MIN_INTENSITY)
                .max(SETS_WEIGHT).`as`(MAX_INTENSITY)
                .avg(SETS_WEIGHT).`as`(AVG_INTENSITY)
                .first(BODYWEIGHT).`as`(BODYWEIGHT)
                .sum(SET_BODYWEIGHT_VOLUME).`as`(BODYWEIGHT_VOLUME)
                .avg(SET_BODYWEIGHT_VOLUME).`as`(AVERAGE_BODYWEIGHT_VOLUME),
            project()
                .and(GROUP_KEY).`as`(DATE)
                .and(EXERCISE).`as`(EXERCISE)
                .and(VOLUME).`as`(PARAMETERS_VOLUME)
                .and(AVERAGE_VOLUME).`as`(PARAMETERS_AVERAGE_VOLUME)
                .and(MIN_INTENSITY).`as`(PARAMETERS_MIN_INTENSITY)
                .and(MAX_INTENSITY).`as`(PARAMETERS_MAX_INTENSITY)
                .and(AVG_INTENSITY).`as`(PARAMETERS_AVG_INTENSITY)
                .and(BODYWEIGHT).`as`(PARAMETERS_BODYWEIGHT)
                .and(BODYWEIGHT_VOLUME).`as`(PARAMETERS_BODYWEIGHT_VOLUME)
                .and(AVERAGE_BODYWEIGHT_VOLUME).`as`(PARAMETERS_AVERAGE_BODYWEIGHT_VOLUME),
            sort(Direction.ASC, DATE),
            group(EXERCISE)
                .push(BasicDBObject()
                    .append(DATE, "\$$DATE")
                    .append(PARAMETERS, "\$$PARAMETERS"))
                .`as`(DATA),
            project()
                .and(GROUP_KEY).`as`(EXERCISE)
                .and(DATA).`as`(PARAMETERS)
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, ExerciseSummary::class.java)
            .next()
    }
}