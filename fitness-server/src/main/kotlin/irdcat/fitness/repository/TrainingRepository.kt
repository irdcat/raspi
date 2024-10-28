package irdcat.fitness.repository

import irdcat.fitness.model.Training
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDate

@Repository
interface TrainingRepository: ReactiveMongoRepository<Training, String> {

    @Query("{ 'exercises.exerciseId': { \$in: ?0 }, 'date': { \$gte: ?1, \$lte: ?2 } }")
    fun findByExerciseIdsFromDate(exerciseIds: List<String>, from: LocalDate, to: LocalDate): Flux<Training>
}