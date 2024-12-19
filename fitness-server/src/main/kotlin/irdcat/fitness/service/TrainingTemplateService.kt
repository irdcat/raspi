package irdcat.fitness.service

import irdcat.fitness.exception.InvalidTrainingTemplateException
import irdcat.fitness.exception.TrainingTemplateNotFoundException
import irdcat.fitness.model.TrainingTemplateDto
import irdcat.fitness.repository.TrainingTemplateRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TrainingTemplateService(
    private val trainingTemplateRepository: TrainingTemplateRepository
) {
    fun getTrainingTemplates(): Flux<TrainingTemplateDto> {
        return trainingTemplateRepository
            .findAll()
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun getTrainingTemplate(id: String): Mono<TrainingTemplateDto> {
        return trainingTemplateRepository
            .findById(id)
            .map(TrainingTemplateDto::fromTrainingTemplate)
            .switchIfEmpty(
                Mono.error(TrainingTemplateNotFoundException(
                    "No training template with id: $id"
                ))
            )
    }

    fun saveTrainingTemplate(trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        return Mono.just(trainingTemplateDto)
            .map(TrainingTemplateDto::toTrainingTemplate)
            .flatMap { trainingTemplateRepository.save(it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
            .onErrorMap { InvalidTrainingTemplateException(it.message ?: "Invalid training template") }
    }

    fun updateTrainingTemplate(id: String, trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        return Mono.just(trainingTemplateDto)
            .map { it.toTrainingTemplate(id) }
            .flatMap { trainingTemplateRepository.save(it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
            .onErrorMap { InvalidTrainingTemplateException(it.message ?: "Invalid training template") }
    }

    fun deleteTrainingTemplate(id: String): Mono<TrainingTemplateDto> {
        return trainingTemplateRepository
            .deleteById(id)
            .then(Mono.just(TrainingTemplateDto(id)))
    }
}