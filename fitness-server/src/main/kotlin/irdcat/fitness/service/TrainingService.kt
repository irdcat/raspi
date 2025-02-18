package irdcat.fitness.service

import com.mongodb.BasicDBObject
import org.springframework.data.domain.Sort
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Date

@Service
class TrainingService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    fun findTrainingsBetweenDates(from: Date, to: Date, page: Int, pageSize: Int): Flux<TrainingDto> {
        val dateCriteria = Criteria.where("date").gte(from).lte(to)
        val matchOperation = match(dateCriteria)
        val groupOperation = group("date")
            .first("bodyweight").`as`("bodyweight")
            .first("date").`as`("date")
            .push(
                BasicDBObject()
                    .append("_id", "\$_id")
                    .append("exercise", "\$exercise")
                    .append("sets", "\$sets")
            ).`as`("exercises")
        val projectionOperation = project()
            .and("date").`as`("date")
            .and("bodyweight").`as`("bodyweight")
            .and("exercises").`as`("exercises")
        val sortOperation = sort(Sort.Direction.DESC, "date")
        val skipOperation = skip((page * pageSize).toLong())
        val limitOperation = limit(pageSize.toLong())

        val aggregation = newAggregation(
            matchOperation,
            groupOperation,
            projectionOperation,
            sortOperation,
            skipOperation,
            limitOperation)
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
    }

    fun findByDate(date: Date): Mono<TrainingDto> {
        val matchOperation = match(Criteria.where("date").`is`(date))
        val groupOperation = group("date")
            .first("bodyweight").`as`("bodyweight")
            .first("date").`as`("date")
            .push(
                BasicDBObject()
                    .append("_id", "\$_id")
                    .append("exercise", "\$exercise")
                    .append("sets", "\$sets")
            ).`as`("exercises")
        val projectionOperation = project()
            .and("date").`as`("date")
            .and("bodyweight").`as`("bodyweight")
            .and("exercises").`as`("exercises")

        val aggregation = newAggregation(matchOperation, groupOperation, projectionOperation)
        return reactiveMongoTemplate
            .aggregate(aggregation, TrainingExercise::class.java, TrainingDto::class.java)
            .next()
    }
}