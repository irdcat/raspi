package irdcat.fitness.repository

import irdcat.fitness.model.TrainingTemplate
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingTemplateRepository: ReactiveMongoRepository<TrainingTemplate, String>