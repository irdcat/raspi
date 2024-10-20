package irdcat.fitness.repository

import irdcat.fitness.model.ExerciseType
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ExerciseTypeRepository : ReactiveMongoRepository<ExerciseType, String>