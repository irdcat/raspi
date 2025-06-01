package irdcat.fitness.service

import irdcat.fitness.service.dto.CountedExerciseDto
import irdcat.fitness.service.model.TrainingExercise
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators.Max
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.addField
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
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
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class ExerciseService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

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
    }

    fun findAllNames(): Mono<List<String>> {
        return reactiveMongoTemplate
            .findDistinct(EXERCISE_NAME, TrainingExercise::class.java, String::class.java)
            .collectList()
            .doOnNext { logger.debug("Exercise names: {}", it) }
    }

    fun findCountedByName(
        name: String?, page: Long, pageSize: Long): Mono<Page<CountedExerciseDto>> {

        val nameCriteria = sanitizeRegexCriteria(name.orEmpty())
        val matchOperation = match(where(EXERCISE_NAME).regex(".*$nameCriteria.*", "i"))
        val singleRepSetsAddFieldOperation = addField(SINGLE_REP_SETS)
            .withValue(
                Filter.filter(SETS)
                    .`as`(SET)
                    .by(Eq.valueOf("\$\$${SET_REPETITIONS}").equalToValue(1)))
            .build()
        val nonSingleRepSetsAddFieldOperation = addField(NON_SINGLE_REP_SETS)
            .withValue(
                Filter.filter(SETS)
                    .`as`(SET)
                    .by(Ne.valueOf("\$\$${SET_REPETITIONS}").notEqualToValue(1)))
            .build()
        val addMaybeBestOperation = addField(MAYBE_BEST)
            .withValue(
                `when`(Gt.valueOf(Size.lengthOfArray(SINGLE_REP_SETS)).greaterThanValue(0))
                    .then(Max.maxOf(SINGLE_REP_SETS_WEIGHT))
                    .otherwise(-1.0f))
            .build()
        val nonSingleRepsBestAddFieldOperation = addField(NON_BEST)
            .withValue(
                `when`(Gt.valueOf(Size.lengthOfArray(NON_SINGLE_REP_SETS)).greaterThanValue(0))
                    .then(Max.maxOf(NON_SINGLE_REP_SETS_WEIGHT))
                    .otherwise(-1.0f))
            .build()
        val groupOperation = group(EXERCISE)
            .count().`as`(COUNT)
            .max(MAYBE_BEST).`as`(MAYBE_BEST)
            .max(NON_BEST).`as`(NON_BEST)
        val projectionOperation = project()
            .and(GROUP_KEY).`as`(EXERCISE)
            .andInclude(COUNT)
            .andInclude(MAYBE_BEST)
            .andInclude(NON_BEST)
        val addBestOperation = addField(BEST)
            .withValue(
                `when`(Gte.valueOf(NON_BEST).greaterThanEqualTo(MAYBE_BEST))
                    .then(-1.0f)
                    .otherwiseValueOf(MAYBE_BEST))
            .build()
        val projection2Operation = project()
            .andInclude(EXERCISE)
            .andInclude(COUNT)
            .andInclude(BEST)
        val sortOperation = sort(Direction.ASC, EXERCISE_NAME)
        val skipOperation = skip((page * pageSize).toLong())
        val limitOperation = limit(pageSize.toLong())
        val countingGroupOperation = group()
            .count().`as`(COUNT)

        val aggregation = newAggregation(
            matchOperation,
            singleRepSetsAddFieldOperation,
            nonSingleRepSetsAddFieldOperation,
            addMaybeBestOperation,
            nonSingleRepsBestAddFieldOperation,
            groupOperation,
            projectionOperation,
            addBestOperation,
            projection2Operation,
            sortOperation,
            skipOperation,
            limitOperation)

        val countingAggregation = newAggregation(
            matchOperation,
            groupOperation,
            countingGroupOperation)

        data class CountIntermediate(val count: Long)

        logger.debug("Aggregation: {}", aggregation)

        val resultsMono = reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, CountedExerciseDto::class.java)
            .map { CountedExerciseDto(it.exercise, it.best.takeIf { b -> b!! > 0.0f }, it.count) }
            .collectList()

        val countMono = reactiveMongoTemplate
            .aggregate(countingAggregation, TrainingExercise::class.java, CountIntermediate::class.java)
            .next()

        return resultsMono
            .zipWith(countMono)
            .map { Page(it.t1, page, pageSize, it.t2.count) }
            .switchIfEmpty(Page(listOf<CountedExerciseDto>(), page, pageSize, 0).toMono())
            .doOnNext {
                logger.debug(
                    "CountedExercise Page: [page={}, size={}, totalResults={}]",
                    it.currentPage, it.pageSize, it.totalResults)
            }
    }

    private fun sanitizeRegexCriteria(value: String): String {
        return value.replace("/[#-.]|[\\[-^]|[?|{}]/g".toRegex(), "\\$&")
    }
}
