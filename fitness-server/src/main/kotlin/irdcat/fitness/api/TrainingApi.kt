package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.exception.TrainingNotFoundException
import irdcat.fitness.service.Page
import irdcat.fitness.service.TrainingDto
import irdcat.fitness.service.TrainingService
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@RestController
@RequestMapping("/api/trainings")
class TrainingApi(
    private val trainingService: TrainingService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun findBetweenDates(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: LocalDate,
        @RequestParam(RequestParameters.PAGE, defaultValue = "0") page: Long,
        @RequestParam(RequestParameters.SIZE, defaultValue = "20") pageSize: Long): Mono<Page<TrainingDto>> {

        logger.info("Requested trainings [from={}, to={}, page={}, size={}]", from, to, page, pageSize)
        return trainingService
            .findTrainingsBetweenDates(from, to, page, pageSize)
    }

    @GetMapping("/{date}", produces = [APPLICATION_JSON_VALUE])
    fun findByDate(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) date: LocalDate): Mono<TrainingDto> {

        logger.info("Requested training [date={}]", date)
        return trainingService
            .findByDate(date)
    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun createOrUpdate(@RequestBody trainingDto: TrainingDto): Mono<TrainingDto> {

        logger.info("Creating or updating training {}", trainingDto)
        return trainingService.createOrUpdate(trainingDto)
    }

    @DeleteMapping("/{date}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTrainingByDate(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) date: LocalDate): Mono<Void> {

        logger.info("Deleting training at {}", date)
        return trainingService.deleteByDate(date)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingNotFoundException::class)
    fun trainingNotFound(
        exception: TrainingNotFoundException,
        serverHttpRequest: ServerHttpRequest): Mono<ErrorResponse> {

        return ErrorResponse
            .fromThrowable(exception, serverHttpRequest, HttpStatus.NOT_FOUND.value())
            .toMono()
    }
}