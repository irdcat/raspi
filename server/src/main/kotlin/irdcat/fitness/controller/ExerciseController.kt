package irdcat.fitness.controller

import irdcat.fitness.exception.*
import irdcat.fitness.model.ExerciseDto
import irdcat.fitness.model.ExerciseFilterDto
import irdcat.fitness.model.ExerciseTypeDto
import irdcat.fitness.service.ExerciseService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = ["http://localhost:5173"])
class ExerciseController(
    private val exerciseService: ExerciseService
) {

    @GetMapping
    fun getAllExercises() : Flux<ExerciseDto> {
        return exerciseService.getExercises()
    }

    @GetMapping("/{id}")
    fun getExercise(@PathVariable id: String) : Mono<ExerciseDto> {
        return exerciseService.getExercise(id)
    }

    @PostMapping("/filter")
    fun filterExercises(@RequestBody exerciseFilterDto: ExerciseFilterDto) : Flux<ExerciseDto> {
        return exerciseService.filterExercises(exerciseFilterDto)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addExercise(@RequestBody exerciseDto: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseService.saveExercise(exerciseDto)
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateExercise(@RequestBody exerciseDto: ExerciseDto) : Mono<ExerciseDto> {
        return exerciseService.updateExercise(exerciseDto)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExerciseNotFoundException::class)
    private fun exerciseNotFoundException(
        exception: ExerciseNotFoundException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidExerciseException::class)
    private fun invalidExerciseException(
        exception: InvalidExerciseException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }

    @GetMapping("/types")
    fun getExerciseTypes() : Flux<ExerciseTypeDto> {
        return exerciseService.getExerciseTypes()
    }

    @GetMapping("/types/{id}")
    fun getExerciseType(@PathVariable id: String) : Mono<ExerciseTypeDto> {
        return exerciseService.getExerciseType(id)
    }

    @PostMapping("/types", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addExerciseType(@RequestBody exerciseTypeDto: ExerciseTypeDto) : Mono<ExerciseTypeDto> {
        return exerciseService.saveExerciseType(exerciseTypeDto)
    }

    @PutMapping("/types", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateExerciseType(@RequestBody exerciseTypeDto: ExerciseTypeDto) : Mono<ExerciseTypeDto> {
        return exerciseService.updateExerciseType(exerciseTypeDto)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExerciseTypeNotFoundException::class)
    private fun exerciseTypeNotFoundException(
        exception: ExerciseTypeNotFoundException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidExerciseTypeException::class)
    private fun invalidExerciseTypeException(
        exception: InvalidExerciseTypeException,
        serverHttpRequest: ServerHttpRequest) : ErrorMessage {
        return ErrorMessage.fromThrowable(exception, serverHttpRequest)
    }
}