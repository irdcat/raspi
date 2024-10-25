package irdcat.fitness.repository

import irdcat.fitness.model.Training
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingRepository: ReactiveMongoRepository<Training, String>