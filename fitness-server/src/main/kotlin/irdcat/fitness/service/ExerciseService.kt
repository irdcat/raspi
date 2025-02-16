package irdcat.fitness.service

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ExerciseService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun findAllNames(): Flux<String> {
        return Flux.empty()
    }

    fun findCountedByName(name: String, page: Int, pageSize: Int): Flux<CountedExerciseDto> {
        val matchOperation = match(Criteria.where("exercise.name").regex(name, "i"))
        val groupOperation = group("exercise").count().`as`("count")
        val sortOperation = sort(Sort.Direction.ASC, "exercise.name")
        val skipOperation = skip((page * pageSize).toLong())
        val limitOperation = limit(pageSize.toLong())
        val aggregation = newAggregation(
            matchOperation, groupOperation, sortOperation, skipOperation, limitOperation)
        return reactiveMongoTemplate.aggregate(
            aggregation, TrainingExercise::class.java, CountedExerciseDto::class.java)
    }
}