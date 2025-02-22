package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.service.Page
import irdcat.fitness.service.TrainingDto
import irdcat.fitness.service.TrainingService
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.Date

@RestController
@RequestMapping("/api/trainings", produces = [APPLICATION_JSON_VALUE])
class TrainingApi(
    private val trainingService: TrainingService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @GetMapping
    fun findBetweenDates(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: Date,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: Date,
        @RequestParam(RequestParameters.PAGE, defaultValue = "0") page: Long,
        @RequestParam(RequestParameters.SIZE, defaultValue = "20") pageSize: Long): Mono<Page<TrainingDto>> {

        logger.info("Requested trainings [from={}, to={}, page={}, size={}]", from, to, page, pageSize)
        return trainingService
            .findTrainingsBetweenDates(from, to, page, pageSize)
    }

    @GetMapping("/{date}")
    fun findByDate(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) date: Date): Mono<TrainingDto> {

        logger.info("Requested training [date={}]", date)
        return trainingService.findByDate(date)
    }

    @PostMapping
    fun createOrUpdate(@RequestBody trainingDto: TrainingDto): Mono<TrainingDto> {

        logger.info("Creating or updating training {}", trainingDto)
        return trainingService.createOrUpdate(trainingDto)
    }
}