package irdcat.fitness.api

import irdcat.fitness.service.TrainingDto
import irdcat.fitness.service.TrainingService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Date

@RestController
@RequestMapping("/api/trainings", produces = [APPLICATION_JSON_VALUE])
class TrainingApi(
    private val trainingService: TrainingService
) {

    @GetMapping
    fun findBetweenDates(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: Date?,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: Date?,
        @RequestParam page: Int,
        @RequestParam pageSize: Int): Flux<TrainingDto> {

        return trainingService.findTrainingsBetweenDates(from, to, page, pageSize)
    }

    @GetMapping("/{date}")
    fun findByDate(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) date: Date): Mono<TrainingDto> {

        return trainingService.findByDate(date)
    }
}