package irdcat.fitness.service

import irdcat.fitness.exception.InvalidTrainingException
import irdcat.fitness.exception.TrainingNotFoundException
import irdcat.fitness.model.ExerciseParametersDto
import irdcat.fitness.model.ExerciseSummaryDto
import irdcat.fitness.model.TrainingDto
import irdcat.fitness.repository.TrainingRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.LocalDate

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

    fun getExerciseSummaries(
        exerciseIds: List<String>,
        from: LocalDate,
        to: LocalDate): Flux<ExerciseSummaryDto> {

        return trainingRepository
            .findByExerciseIdsFromDate(exerciseIds, from, to)
            .map { Pair(it.date, it.exercises) }
            .collectList()
            .flatMapMany {
                it.flatMap { (key, values) ->
                    values.map { e -> key to e }
                }.toFlux()
            }
            .groupBy { it.second.exerciseId }
            .flatMap { grouped ->
                grouped
                    .map { it.first to ExerciseParametersDto.fromTrainingExercise(it.second) }
                    .collectList()
                    .map { ExerciseSummaryDto(grouped.key(), it.associate { (k, v) -> k to v }) }
            }
            .filter { exerciseIds.contains(it.id) }
    }
}