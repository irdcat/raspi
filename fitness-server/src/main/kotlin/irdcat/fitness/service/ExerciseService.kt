package irdcat.fitness.service

import com.mongodb.BasicDBObject
import org.springframework.data.domain.PageRequest
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

    fun findCountedByName(name: String?, page: Long, pageSize: Long): Mono<Page<CountedExerciseDto>> {

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
        val groupEverythingOperation = group()
            .count().`as`("totalCount")
            .push(BasicDBObject()
                .append("exercise", "\$exercise")
                .append("count", "\$count"))
            .`as`("results")

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            projectionOperation,
            sortOperation,
            skipOperation,
            limitOperation,
            groupEverythingOperation)

        data class AggregateResult(
            val results: List<CountedExerciseDto>,
            val totalCount: Long
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, AggregateResult::class.java)
            .next()
            .map { Page(it.results, page, pageSize, it.totalCount) }
    }

    private fun sanitizeRegexCriteria(value: String): String {
        return value.replace("/[#-.]|[\\[-^]|[?|{}]/g".toRegex(), "\\$&")
    }
}