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
interface ExerciseRepository : ReactiveMongoRepository<Exercise, String>