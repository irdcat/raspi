package irdcat.fitness.service

import irdcat.fitness.exception.ExerciseNotFoundException
import irdcat.fitness.exception.ExerciseTypeNotFoundException
import irdcat.fitness.exception.InvalidExerciseException
import irdcat.fitness.exception.InvalidExerciseTypeException
import irdcat.fitness.model.ExerciseDto
import irdcat.fitness.model.ExerciseTypeDto
import irdcat.fitness.repository.ExerciseRepository
import irdcat.fitness.repository.ExerciseTypeRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Date

@Service
class ExerciseService(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseTypeRepository: ExerciseTypeRepository
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

    fun getExercisesBetweenDates(from: Date, to: Date) : Flux<ExerciseDto> {
        return exerciseRepository
            .findBetweenDates(from, to, Sort.by(Sort.Direction.DESC, "date"))
            .map(ExerciseDto::fromExercise)
    }

    fun getExercisesForTypeBetweenDates(typeId: String, from: Date, to: Date) : Flux<ExerciseDto> {
        return exerciseRepository
            .findByTypeIdBetweenDates(typeId, from, to, Sort.by(Sort.Direction.DESC, "date"))
            .map(ExerciseDto::fromExercise)
    }

    fun getExercisesForTypeId(typeId: String) : Flux<ExerciseDto> {
        return exerciseRepository
            .findByTypeId(typeId, Sort.by(Sort.Direction.DESC, "date"))
            .map(ExerciseDto::fromExercise)
    }

    fun saveExercise(exerciseDto: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseRepository
            .save(exerciseDto.toExercise())
            .map(ExerciseDto::fromExercise)
    }

    fun updateExercise(exerciseDto: ExerciseDto) : Mono<ExerciseDto> {
        return Mono.just(exerciseDto)
            .filter { it.id.isNotBlank() && it.id.isNotEmpty() }
            .filter { it.typeId.isNotBlank() && it.typeId.isNotEmpty() }
            .map { it.toExercise() }
            .flatMap { exerciseRepository.save(it) }
            .map(ExerciseDto::fromExercise)
            .switchIfEmpty(Mono.error(InvalidExerciseException("Attempting to edit exercise with incomplete data $exerciseDto")))
    }

    fun getExerciseTypes() : Flux<ExerciseTypeDto> {
        return exerciseTypeRepository
            .findAll()
            .map(ExerciseTypeDto::fromExerciseType)
    }

    fun getExerciseType(id: String) : Mono<ExerciseTypeDto> {
        return exerciseTypeRepository
            .findById(id)
            .map(ExerciseTypeDto::fromExerciseType)
            .switchIfEmpty(Mono.error(ExerciseTypeNotFoundException("No exercise type with id $id")))
    }

    fun saveExerciseType(exerciseTypeDto: ExerciseTypeDto) : Mono<ExerciseTypeDto> {
        return exerciseTypeRepository
            .save(exerciseTypeDto.toExerciseType())
            .map(ExerciseTypeDto::fromExerciseType)
    }

    fun updateExerciseType(exerciseTypeDto: ExerciseTypeDto) : Mono<ExerciseTypeDto> {
        return Mono.just(exerciseTypeDto)
            .filter { it.id.isNotBlank() && it.id.isNotEmpty() }
            .map { it.toExerciseType() }
            .flatMap { exerciseTypeRepository.save(it) }
            .map(ExerciseTypeDto::fromExerciseType)
            .switchIfEmpty(Mono.error(InvalidExerciseTypeException("Attempting to edit exercise type without id")))
    }
}