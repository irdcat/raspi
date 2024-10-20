package irdcat.fitness.repository

import irdcat.fitness.model.Exercise
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.Date

@Repository
interface ExerciseRepository : ReactiveMongoRepository<Exercise, String> {

    @Query("{'typeId' : ?0, 'date': { \$gte: ?1, \$lte: ?2 } }")
    fun findByTypeIdBetweenDates(typeId: String, from: Date, to: Date, sort: Sort) : Flux<Exercise>

    fun findByTypeId(typeId: String, sort: Sort) : Flux<Exercise>

    @Query("'date': { \$gte: ?1, \$lte: ?2 }")
    fun findBetweenDates(from: Date, to: Date, sort: Sort) : Flux<Exercise>
}