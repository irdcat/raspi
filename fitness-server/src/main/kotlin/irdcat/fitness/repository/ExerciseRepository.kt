package irdcat.fitness.repository

import irdcat.fitness.repository.model.ValueHolder
import irdcat.fitness.service.dto.CountedExerciseDto
import irdcat.fitness.service.model.TrainingExercise
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Max
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.addField
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond.`when`
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Size
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gt
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Gte
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Ne
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ExerciseRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        const val SETS = "sets"
        const val SET = "set"
        const val SET_REPETITIONS = "set.repetitions"
        const val SINGLE_REP_SETS = "singleRepSets"
        const val NON_SINGLE_REP_SETS = "nonSingleRepSets"
        const val SINGLE_REP_SETS_WEIGHT = "singleRepSets.weight"
        const val NON_SINGLE_REP_SETS_WEIGHT = "nonSingleRepSets.weight"
        const val EXERCISE = "exercise"
        const val EXERCISE_NAME = "exercise.name"
        const val COUNT = "count"
        const val MAYBE_BEST = "maybeBest"
        const val BEST = "best"
        const val NON_BEST = "nonBest"
        const val GROUP_KEY = "_id"
        const val VALUE = "value"
    }

    fun findDistinctNames(): Flux<String> {
        return reactiveMongoTemplate
            .findDistinct(EXERCISE_NAME, TrainingExercise::class.java, String::class.java)
    }

    fun findCountedByName(name: String?, skip: Long, limit: Long): Flux<CountedExerciseDto> {
        val sanitizedNameRegexPart = sanitizeRegexCriteria(name.orEmpty())
        val aggregation = newAggregation(
            match(where(EXERCISE_NAME).regex(".*${sanitizedNameRegexPart}.*", "i")),
            addField(SINGLE_REP_SETS)
                .withValue(
                    Filter.filter(SETS)
                        .`as`(SET)
                        .by(Eq.valueOf("\$\$${SET_REPETITIONS}").equalToValue(1)))
                .addField(NON_SINGLE_REP_SETS)
                .withValue(Filter.filter(SETS)
                    .`as`(SET)
                    .by(Ne.valueOf("\$\$${SET_REPETITIONS}").notEqualToValue(1)))
                .build(),
            addField(MAYBE_BEST)
                .withValue(
                    `when`(Gt.valueOf(Size.lengthOfArray(SINGLE_REP_SETS)).greaterThanValue(0))
                        .then(Max.maxOf(SINGLE_REP_SETS_WEIGHT))
                        .otherwise(-1.0f))
                .addField(NON_BEST)
                .withValue(
                    `when`(Gt.valueOf(Size.lengthOfArray(NON_SINGLE_REP_SETS)).greaterThanValue(0))
                        .then(Max.maxOf(NON_SINGLE_REP_SETS_WEIGHT))
                        .otherwise(-1.0f))
                .build(),
            group(EXERCISE)
                .count().`as`(COUNT)
                .max(MAYBE_BEST).`as`(MAYBE_BEST)
                .max(NON_BEST).`as`(NON_BEST),
            project()
                .and(GROUP_KEY).`as`(EXERCISE)
                .andInclude(COUNT)
                .andInclude(MAYBE_BEST)
                .andInclude(NON_BEST),
            addField(BEST)
                .withValue(
                    `when`(Gte.valueOf(NON_BEST).greaterThanEqualTo(MAYBE_BEST))
                        .then(-1.0f)
                        .otherwiseValueOf(MAYBE_BEST))
                .build(),
            project()
                .andInclude(EXERCISE)
                .andInclude(COUNT)
                .andInclude(BEST),
            sort(Direction.ASC, EXERCISE_NAME),
            skip(skip),
            limit(limit)
        )

        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, CountedExerciseDto::class.java)
            .map { CountedExerciseDto(it.exercise, it.best.takeIf { b -> b!! > 0.0f }, it.count) }
    }

    fun countByName(name: String?): Mono<ValueHolder<Long>> {
        val sanitizedNameRegexPart = sanitizeRegexCriteria(name.orEmpty())
        val aggregation = newAggregation(
            match(where(EXERCISE_NAME).regex(".*${sanitizedNameRegexPart}.*", "i")),
            group(EXERCISE),
            group()
                .count().`as`(VALUE))

        val valueHolderClass = ValueHolder<Long>(0L).javaClass
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, valueHolderClass)
            .next()
    }

    private fun sanitizeRegexCriteria(value: String): String {
        return value.replace("/[#-.]|[\\[-^]|[?|{}]/g".toRegex(), "\\$&")
    }
}