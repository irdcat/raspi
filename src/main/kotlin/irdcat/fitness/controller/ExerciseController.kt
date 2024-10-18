package irdcat.fitness.controller

import irdcat.fitness.exception.ErrorMessage
import irdcat.fitness.exception.ExerciseNotFoundException
import irdcat.fitness.exception.ExerciseTypeNotFoundException
import irdcat.fitness.model.ExerciseDTO
import irdcat.fitness.model.ExerciseTypeDTO
import irdcat.fitness.service.ExerciseService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    fun getAllExercises() : Flux<ExerciseDTO> {
        return exerciseService.getExercises()
    }

    @GetMapping("/{id}")
    fun getExercise(@PathVariable id: String) : Mono<ExerciseDTO> {
        return exerciseService.getExercise(id)
    }

    @PostMapping
    fun addExercise(@RequestBody exerciseDTO: ExerciseDTO) : Mono<ExerciseDTO> {
        return exerciseService.addExercise(exerciseDTO)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExerciseNotFoundException::class)
    private fun exerciseNotFoundException(
        exception: ExerciseNotFoundException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }

    @GetMapping("/types")
    fun getExerciseTypes() : Flux<ExerciseTypeDTO> {
        return exerciseService.getExerciseTypes()
    }

    @GetMapping("/types/{id}")
    fun getExerciseType(@PathVariable id: String) : Mono<ExerciseTypeDTO> {
        return exerciseService.getExerciseType(id)
    }

    @PostMapping("/types")
    fun addExerciseType(@RequestBody exerciseTypeDTO: ExerciseTypeDTO) : Mono<ExerciseTypeDTO> {
        return exerciseService.addExerciseType(exerciseTypeDTO)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExerciseTypeNotFoundException::class)
    private fun exerciseTypeNotFoundException(
        exception: ExerciseTypeNotFoundException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }
}