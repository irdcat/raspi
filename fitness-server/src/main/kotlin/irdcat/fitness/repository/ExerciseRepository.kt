package irdcat.fitness.repository

import irdcat.fitness.model.Exercise
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ExerciseRepository : ReactiveMongoRepository<Exercise, String>