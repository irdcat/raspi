package irdcat.fitness.service

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.addFields
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply.valueOf
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.Date

@Service
class SummaryService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun calculateSummary(from: Date?, to: Date?, name: String?, fields: List<String>): Mono<SummaryDto> {
        val criteria = Criteria()
        if (from != null && to != null) {
            criteria.and("date").gte(from).lte(to)
        } else if (from != null) {
            criteria.and("date").gte(from)
        } else if (to != null) {
            criteria.and("date").lte(to)
        }
        if (!name.isNullOrBlank()) {
            criteria.and("exercise.name").`is`(name)
        }

        val matchOperation = match(criteria)
        val unwindOperation = unwind("sets")
        val addFieldsOperation = addFields()
            .addField("setVolume").withValue(valueOf("sets.repetitions").multiplyBy("sets.weight"))
            .addField("setBodyweightVolume").withValue(valueOf("sets.repetitions").multiplyBy("bodyweight"))
            .build()
        val groupOperation = if (!name.isNullOrBlank()) {
            group("exercise", "date")
                .sum("setVolume").`as`("volume")
                .avg("setVolume").`as`("averageVolume")
                .sum("setBodyweightVolume").`as`("bodyweightVolume")
                .min("sets.weight").`as`("minIntensity")
                .max("sets.weight").`as`("maxIntensity")
                .avg("sets.weight").`as`("averageIntensity")
                .first("bodyweight").`as`("bodyweight")
        } else {
            group("date")
                .sum("setVolume").`as`("volume")
                .avg("setVolume").`as`("averageVolume")
                .sum("setBodyweightVolume").`as`("bodyweightVolume")
                .min("sets.weight").`as`("minIntensity")
                .max("sets.weight").`as`("maxIntensity")
                .avg("sets.weight").`as`("averageIntensity")
                .first("bodyweight").`as`("bodyweight")
        }
        val projectionOperation = if(!name.isNullOrBlank()) {
            project()
                .and("_id.exercise").`as`("exercise")
                .and("_id.date").`as`("date")
                .andIncludeSelectedFields(fields)
        } else {
            project()
                .and("_id.date").`as`("date")
                .andIncludeSelectedFields(fields)
        }
        val aggregation = newAggregation(
            matchOperation, unwindOperation, addFieldsOperation, groupOperation, projectionOperation)
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, SummaryDto::class.java)
            .next()
    }

    private fun ProjectionOperation.andIncludeSelectedFields(fields: List<String>): ProjectionOperation {
        fields.forEach { field ->
            when (field) {
                "volume" -> this.and("volume").`as`("volume")
                "averageVolume" -> this.and("averageVolume").`as`("averageVolume")
                "bodyweightVolume" -> this.and("bodyweightVolume").`as`("bodyweightVolume")
                "minIntensity" -> this.and("minIntensity").`as`("minIntensity")
                "maxIntensity" -> this.and("maxIntensity").`as`("maxIntensity")
                "averageIntensity" -> this.and("averageIntensity").`as`("averageIntensity")
                "bodyweight" -> this.and("bodyweight").`as`("bodyweight")
            }
        }
        return this
    }
}