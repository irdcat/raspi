package irdcat.fitness.api

import irdcat.fitness.service.dto.BodyweightSummaryDto
import irdcat.fitness.service.dto.ExerciseSummaryDto
import irdcat.fitness.service.SummaryService
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDate

@RestController
@RequestMapping("/api/summary", produces = [APPLICATION_JSON_VALUE])
class SummaryApi(
    private val summaryService: SummaryService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @GetMapping("/exercise")
    fun getExerciseSummary(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: LocalDate,
        @RequestParam name: String): Mono<ExerciseSummaryDto> {

        logger.info("Requested exercise summary [name={}, from={}, to={}]", name, from, to)
        return summaryService.calculateExerciseSummary(from, to, name)
    }

    @GetMapping("/bodyweight")
    fun getBodyweightSummary(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: LocalDate): Mono<BodyweightSummaryDto> {

        logger.info("Requested bodyweight summary [from={}, to={}]", from, to)
        return summaryService.calculateBodyweightSummary(from, to)
    }
}
