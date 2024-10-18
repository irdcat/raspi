package irdcat.fitness.service

import irdcat.fitness.exception.ExerciseNotFoundException
import irdcat.fitness.exception.ExerciseTypeNotFoundException
import irdcat.fitness.model.ExerciseDTO
import irdcat.fitness.model.ExerciseTypeDTO
import irdcat.fitness.repository.ExerciseRepository
import irdcat.fitness.repository.ExerciseTypeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ExerciseService(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseTypeRepository: ExerciseTypeRepository
) {

    fun getExerciseTypes() : Flux<ExerciseTypeDTO> {
        return exerciseTypeRepository
            .findAll()
            .map(ExerciseTypeDTO::fromExerciseType)
            .switchIfEmpty(Mono.error(ExerciseTypeNotFoundException("No exercise types found")))
    }

    fun getExerciseType(id: String) : Mono<ExerciseTypeDTO> {
        return exerciseTypeRepository
            .findById(id)
            .map(ExerciseTypeDTO::fromExerciseType)
            .switchIfEmpty(Mono.error(ExerciseTypeNotFoundException("No exercise type with id $id")))
    }

    fun addExerciseType(exerciseTypeDTO: ExerciseTypeDTO) : Mono<ExerciseTypeDTO> {
        return exerciseTypeRepository
            .insert(exerciseTypeDTO.toExerciseType())
            .map(ExerciseTypeDTO::fromExerciseType)
    }

    fun getExercises() : Flux<ExerciseDTO> {
        return exerciseRepository
            .findAll()
            .map(ExerciseDTO::fromExercise)
            .switchIfEmpty(Mono.error(ExerciseNotFoundException("No exercises found")))
    }

    fun getExercise(id: String) : Mono<ExerciseDTO> {
        return exerciseRepository
            .findById(id)
            .map(ExerciseDTO::fromExercise)
            .switchIfEmpty(Mono.error(ExerciseNotFoundException("No exercise with id $id")))
    }

    fun addExercise(exerciseDTO: ExerciseDTO) : Mono<ExerciseDTO> {
        return exerciseRepository
            .insert(exerciseDTO.toExercise())
            .map(ExerciseDTO::fromExercise)
    }
}