package irdcat.fitness.service

import irdcat.fitness.exception.InvalidTrainingException
import irdcat.fitness.exception.TrainingNotFoundException
import irdcat.fitness.model.TrainingDto
import irdcat.fitness.repository.TrainingRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TrainingService(
    private val trainingRepository: TrainingRepository
) {

    fun getTrainings(): Flux<TrainingDto> {
        return trainingRepository
            .findAll()
            .map(TrainingDto::fromTraining)
    }

    fun getTraining(id: String): Mono<TrainingDto> {
        return trainingRepository
            .findById(id)
            .map(TrainingDto::fromTraining)
            .switchIfEmpty(Mono.error(TrainingNotFoundException("No training with id $id")))
    }

    fun saveTraining(trainingDto: TrainingDto): Mono<TrainingDto> {
        return Mono.just(trainingDto)
            .map { it.toTraining() }
            .flatMap { trainingRepository.save(it) }
            .map(TrainingDto::fromTraining)
            .onErrorMap { InvalidTrainingException(it.message ?: "Invalid training") }
    }

    fun updateTraining(id: String, trainingDto: TrainingDto): Mono<TrainingDto> {
        return Mono.just(trainingDto)
            .map { it.toTraining(id) }
            .flatMap { trainingRepository.save(it) }
            .map(TrainingDto::fromTraining)
            .onErrorMap { InvalidTrainingException(it.message ?: "Invalid training") }
    }

    fun deleteTraining(id: String): Mono<TrainingDto> {
        return trainingRepository
            .deleteById(id)
            .then(Mono.just(TrainingDto(id)))
    }
}