package irdcat.fitness.api

import irdcat.fitness.service.BodyweightSummaryDto
import irdcat.fitness.service.ExerciseSummaryDto
import irdcat.fitness.service.SummaryService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.Date

@RestController
@RequestMapping("/api/summary", produces = [APPLICATION_JSON_VALUE])
class SummaryApi(
    private val summaryService: SummaryService
) {

    @GetMapping("/exercise")
    fun getExerciseSummary(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: Date,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: Date,
        @RequestParam name: String): Mono<ExerciseSummaryDto> {

        return summaryService.calculateExerciseSummary(from, to, name)
    }

    @GetMapping("/bodyweight")
    fun getBodyweightSummary(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) from: Date,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) to: Date): Mono<BodyweightSummaryDto> {

        return summaryService.calculateBodyweightSummary(from, to)
    }
}