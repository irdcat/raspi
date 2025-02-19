package irdcat.fitness.service

import com.mongodb.BasicDBObject
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
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat
import java.util.Date

@Service
class SummaryService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {

        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

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
        const val PARAMETER = "parameter"
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

    fun calculateExerciseSummary(from: Date, to: Date, name: String): Mono<ExerciseSummaryDto> {

        val criteria = Criteria
            .where(DATE).gte(from).lte(to)
            .and(EXERCISE_NAME).isEqualTo(name)
        val matchOperation = match(criteria)
        val unwindOperation = unwind(SETS)
        val addFieldsOperation = addFields()
            .addField(SET_VOLUME).withValue(valueOf(SETS_REPETITIONS).multiplyBy(SETS_WEIGHT))
            .addField(SET_BODYWEIGHT_VOLUME).withValue(valueOf(SETS_REPETITIONS).multiplyBy(BODYWEIGHT))
            .build()
        val groupByDateOperation = group(DATE)
            .first(EXERCISE).`as`(EXERCISE)
            .sum(SET_VOLUME).`as`(VOLUME)
            .avg(SET_VOLUME).`as`(AVERAGE_VOLUME)
            .min(SETS_WEIGHT).`as`(MIN_INTENSITY)
            .max(SETS_WEIGHT).`as`(MAX_INTENSITY)
            .avg(SETS_WEIGHT).`as`(AVG_INTENSITY)
            .first(BODYWEIGHT).`as`(BODYWEIGHT)
            .sum(SET_BODYWEIGHT_VOLUME).`as`(BODYWEIGHT_VOLUME)
            .avg(SET_BODYWEIGHT_VOLUME).`as`(AVERAGE_BODYWEIGHT_VOLUME)
        val projectByDate = project()
            .and(GROUP_KEY).`as`(DATE)
            .and(EXERCISE).`as`(EXERCISE)
            .and(VOLUME).`as`(PARAMETERS_VOLUME)
            .and(AVERAGE_VOLUME).`as`(PARAMETERS_AVERAGE_VOLUME)
            .and(MIN_INTENSITY).`as`(PARAMETERS_MIN_INTENSITY)
            .and(MAX_INTENSITY).`as`(PARAMETERS_MAX_INTENSITY)
            .and(AVG_INTENSITY).`as`(PARAMETERS_AVG_INTENSITY)
            .and(BODYWEIGHT).`as`(PARAMETERS_BODYWEIGHT)
            .and(BODYWEIGHT_VOLUME).`as`(PARAMETERS_BODYWEIGHT_VOLUME)
            .and(AVERAGE_BODYWEIGHT_VOLUME).`as`(PARAMETERS_AVERAGE_BODYWEIGHT_VOLUME)
        var sortOperation = sort(Direction.ASC, DATE)
        val groupByExercise = group(EXERCISE)
            .push(BasicDBObject()
                .append(DATE, "\$$DATE")
                .append(PARAMETERS, "\$$PARAMETERS"))
            .`as`(DATA)
        val projectByExercise = project()
            .and(GROUP_KEY).`as`(EXERCISE)
            .and(DATA).`as`(DATA)

        val aggregation = newAggregation(
            matchOperation,
            unwindOperation,
            addFieldsOperation,
            groupByDateOperation,
            projectByDate,
            groupByExercise,
            projectByExercise)

        data class IntermediateParametersAggregation(
            val date: Date,
            val parameters: ExerciseSummaryParametersDto
        )

        data class IntermediateAggregation(
            val exercise: ExerciseDto,
            val data: List<IntermediateParametersAggregation>
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, IntermediateAggregation::class.java)
            .next()
            .map {
                ExerciseSummaryDto(
                    it.exercise,
                    it.data.associate { i ->
                        val mappedParams = if (it.exercise.isBodyweight) {
                            i.parameters
                        } else {
                            i.parameters.copy(bodyweightVolume = null, averageBodyweightVolume = null)
                        }
                        dateFormatter.format(i.date) to mappedParams
                    }
                )
            }
    }

    fun calculateBodyweightSummary(from: Date, to: Date): Mono<BodyweightSummaryDto> {

        val criteria = Criteria.where(DATE).gte(from).lte(to)
        val matchOperation = match(criteria)
        val groupOperation = group(DATE)
            .first(BODYWEIGHT).`as`(BODYWEIGHT)
        val projectionOperation = project()
            .and(GROUP_KEY).`as`(DATE)
            .and(BODYWEIGHT).`as`(PARAMETER)
        val sortOperation = sort(Direction.ASC, DATE)

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            projectionOperation,
            sortOperation)

        data class IntermediateAggregation(
            val date: Date,
            val parameter: Float
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, IntermediateAggregation::class.java)
            .map { Pair(dateFormatter.format(it.date), it.parameter.toFloat()) }
            .collectList()
            .map { BodyweightSummaryDto(it.toMap()) }
    }
}