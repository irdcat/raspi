package irdcat.fitness.service

import com.mongodb.BasicDBObject
import org.springframework.data.domain.Sort.Direction.ASC
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
    }

    fun calculateExerciseSummary(from: Date, to: Date, name: String): Mono<ExerciseSummaryDto> {

        // TODO: Tidy up

        val criteria = Criteria
            .where("date").gte(from).lte(to)
            .and("exercise.name").isEqualTo(name)
        val matchOperation = match(criteria)
        val unwindOperation = unwind("sets")
        val addFieldsOperation = addFields()
            .addField("setVolume").withValue(valueOf("sets.repetitions").multiplyBy("sets.weight"))
            .addField("setBodyweightVolume").withValue(valueOf("sets.repetitions").multiplyBy("bodyweight"))
            .build()
        val groupByDateOperation = group("date")
            .first("exercise").`as`("exercise")
            .sum("setVolume").`as`("volume")
            .avg("setVolume").`as`("averageVolume")
            .min("sets.weight").`as`("minIntensity")
            .max("sets.weight").`as`("maxIntensity")
            .avg("sets.weight").`as`("averageIntensity")
            .first("bodyweight").`as`("bodyweight")
            .sum("setBodyweightVolume").`as`("bodyweightVolume")
            .avg("setBodyweightVolume").`as`("averageBodyweightVolume")
        val projectByDate = project()
            .and("_id").`as`("date")
            .and("exercise").`as`("exercise")
            .and("volume").`as`("parameters.volume")
            .and("averageVolume").`as`("parameters.averageVolume")
            .and("minIntensity").`as`("parameters.minIntensity")
            .and("maxIntensity").`as`("parameters.maxIntensity")
            .and("averageIntensity").`as`("parameters.averageIntensity")
            .and("bodyweight").`as`("parameters.bodyweight")
            .and("bodyweightVolume").`as`("parameters.bodyweightVolume")
            .and("averageBodyweightVolume").`as`("parameters.averageBodyweightVolume")
        val groupByExercise = group("exercise")
            .push(BasicDBObject()
                .append("date", "\$date")
                .append("parameters", "\$parameters"))
            .`as`("data")
        val projectByExercise = project()
            .and("_id").`as`("exercise")
            .and("data").`as`("data")

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
            .aggregate(
                aggregation,
                TrainingExercise::class.java,
                IntermediateAggregation::class.java)
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

        // TODO: Declare intermediate aggregation type

        val criteria = Criteria.where("date").gte(from).lte(to)
        val matchOperation = match(criteria)
        val groupOperation = group("date")
            .first("bodyweight").`as`("bodyweight")
        val projectionOperation = project()
            .and("_id").`as`("first")
            .and("bodyweight").`as`("second")
        val sortOperation = sort(ASC, "date")

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            projectionOperation,
            sortOperation)
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, Pair(Date(), 0.0f)::class.java)
            .map { Pair(dateFormatter.format(it.first), it.second.toFloat()) }
            .collectList()
            .map { BodyweightSummaryDto(it.toMap()) }
    }
}