package irdcat.fitness.service

import irdcat.fitness.exception.ExerciseNotFoundException
import irdcat.fitness.exception.InvalidExerciseException
import irdcat.fitness.model.ExerciseDto
import irdcat.fitness.repository.ExerciseRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ExerciseService(
    private val exerciseRepository: ExerciseRepository
) {

    fun getExercises() : Flux<ExerciseDto> {
        return exerciseRepository
            .findAll()
            .map(ExerciseDto::fromExercise)
    }

    fun getExercise(id: String) : Mono<ExerciseDto> {
        return exerciseRepository
            .findById(id)
            .map(ExerciseDto::fromExercise)
            .switchIfEmpty(Mono.error(ExerciseNotFoundException("No exercise with id $id")))
    }

    fun saveExercise(exercise: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseRepository
            .save(exercise.toExercise())
            .map(ExerciseDto::fromExercise)
            .onErrorMap { InvalidExerciseException(it.message ?: "Invalid exercise") }
    }

    fun updateExercise(id: String, exercise: ExerciseDto) : Mono<ExerciseDto> {
        return Mono.just(exercise)
            .map { it.toExercise(id) }
            .flatMap { exerciseRepository.save(it) }
            .map(ExerciseDto::fromExercise)
            .onErrorMap { InvalidExerciseException(it.message ?: "Invalid exercise") }
    }

    fun deleteExercise(id: String) : Mono<ExerciseDto> {
        return exerciseRepository
            .deleteById(id)
            .then(Mono.just(ExerciseDto(id)))
    }
}