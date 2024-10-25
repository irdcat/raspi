package irdcat.fitness.controller

import irdcat.fitness.exception.*
import irdcat.fitness.model.ExerciseDto
import irdcat.fitness.service.ExerciseService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/exercises",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class ExerciseController(
    private val exerciseService: ExerciseService
) {
    
    @GetMapping
    fun getExercises() : Flux<ExerciseDto> {
        return exerciseService.getExercises()
    }

    @GetMapping("/{id}")
    fun getExercise(@PathVariable id: String) : Mono<ExerciseDto> {
        return exerciseService.getExercise(id)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addExercise(@RequestBody exercise: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseService.saveExercise(exercise)
    }

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateExercise(
        @PathVariable id: String,
        @RequestBody exercise: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseService.updateExercise(id, exercise)
    }

    @DeleteMapping("/{id}")
    fun deleteExercise(@PathVariable id: String) : Mono<ExerciseDto> {
        return exerciseService.deleteExercise(id)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExerciseNotFoundException::class)
    private fun exerciseTypeNotFoundException(
        exception: ExerciseNotFoundException,
        serverHttpRequest: ServerHttpRequest) : Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidExerciseException::class)
    private fun invalidExerciseException(
        exception: InvalidExerciseException,
        serverHttpRequest: ServerHttpRequest) : Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }
}