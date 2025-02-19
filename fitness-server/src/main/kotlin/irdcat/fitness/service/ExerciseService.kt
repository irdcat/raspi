package irdcat.fitness.service

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

@Service
class ExerciseService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    companion object {
        const val EXERCISE = "exercise"
        const val EXERCISE_NAME = "exercise.name"
        const val COUNT = "count"
        const val GROUP_KEY = "_id"
    }

    fun findAllNames(): Mono<List<String>> {
        return reactiveMongoTemplate
            .findDistinct(EXERCISE_NAME, TrainingExercise::class.java, String::class.java)
            .collectList()
    }

    fun findCountedByName(
        name: String?, page: Long, pageSize: Long): Mono<Page<CountedExerciseDto>> {

        val nameCriteria = sanitizeRegexCriteria(name.orEmpty())
        val matchOperation = match(Criteria.where(EXERCISE_NAME).regex(".*$nameCriteria.*", "i"))
        val groupOperation = group(EXERCISE)
            .count().`as`(COUNT)
        val projectionOperation = project()
            .and(GROUP_KEY).`as`(EXERCISE)
            .and(COUNT).`as`(COUNT)
        val sortOperation = sort(Direction.ASC, EXERCISE_NAME)
        val skipOperation = skip((page * pageSize).toLong())
        val limitOperation = limit(pageSize.toLong())
        val countingGroupOperation = group()
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
            countingGroupOperation)

        data class CountIntermediate(val count: Long)

        val resultsMono = reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, CountedExerciseDto::class.java)
            .collectList()
        val countMono = reactiveMongoTemplate
            .aggregate(countingAggregation, TrainingExercise::class.java, CountIntermediate::class.java)
            .next()

        return resultsMono
            .zipWith(countMono)
            .map { Page(it.t1, page, pageSize, it.t2.count) }
    }

    private fun sanitizeRegexCriteria(value: String): String {
        return value.replace("/[#-.]|[\\[-^]|[?|{}]/g".toRegex(), "\\$&")
    }
}