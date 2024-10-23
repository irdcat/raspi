package irdcat.fitness.controller

import irdcat.fitness.exception.InvalidTrainingException
import irdcat.fitness.exception.TrainingNotFoundException
import irdcat.fitness.model.TrainingDto
import irdcat.fitness.service.TrainingService
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
@RequestMapping("/api/trainings",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class TrainingController(
    private val trainingService: TrainingService
) {

    @GetMapping
    fun getTrainings(): Flux<TrainingDto> {
        return trainingService.getTrainings()
    }

    @GetMapping("/{id}")
    fun getTraining(@PathVariable id: String): Mono<TrainingDto> {
        return trainingService.getTraining(id)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addTraining(@RequestBody trainingDto: TrainingDto): Mono<TrainingDto> {
        return trainingService.saveTraining(trainingDto)
    }

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTraining(@PathVariable id: String, @RequestBody trainingDto: TrainingDto): Mono<TrainingDto> {
        return trainingService.updateTraining(id, trainingDto)
    }

    @DeleteMapping("/{id}")
    fun deleteTraining(@PathVariable id: String): Mono<TrainingDto> {
        return trainingService.deleteTraining(id)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingNotFoundException::class)
    fun trainingNotFoundException(
        exception: TrainingNotFoundException,
        serverHttpRequest: ServerHttpRequest): Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTrainingException::class)
    fun invalidTrainingException(
        exception: InvalidTrainingException,
        serverHttpRequest: ServerHttpRequest): Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }
}