package irdcat.fitness.controller

import irdcat.fitness.exception.InvalidTrainingTemplateException
import irdcat.fitness.exception.TrainingTemplateNotFoundException
import irdcat.fitness.model.TrainingTemplateDto
import irdcat.fitness.service.TrainingTemplateService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/templates",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class TrainingTemplateController(
    private val trainingTemplateService: TrainingTemplateService
) {

    @GetMapping
    fun getTrainingTemplates(): Flux<TrainingTemplateDto> {
        return trainingTemplateService.getTrainingTemplates()
    }

    @GetMapping("/{id}")
    fun getTrainingTemplate(@PathVariable id: String): Mono<TrainingTemplateDto> {
        return trainingTemplateService.getTrainingTemplate(id);
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addTrainingTemplate(@RequestBody trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        return trainingTemplateService.saveTrainingTemplate(trainingTemplateDto)
    }

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTrainingTemplate(@PathVariable id: String, @RequestBody trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        return trainingTemplateService.updateTrainingTemplate(id, trainingTemplateDto)
    }

    @DeleteMapping("/{id}")
    fun deleteTrainingTemplate(@PathVariable id: String): Mono<TrainingTemplateDto> {
        return trainingTemplateService.deleteTrainingTemplate(id)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingTemplateNotFoundException::class)
    fun trainingTemplateNotFoundException(
        exception: TrainingTemplateNotFoundException,
        serverHttpRequest: ServerHttpRequest) : Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTrainingTemplateException::class)
    fun invalidTrainingTemplateException(
        exception: InvalidTrainingTemplateException,
        serverHttpRequest: ServerHttpRequest): Mono<ErrorResponse> {
        return Mono.just(ErrorResponse.fromThrowable(exception, serverHttpRequest))
    }
}