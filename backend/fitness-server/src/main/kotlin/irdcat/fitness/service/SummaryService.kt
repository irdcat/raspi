package irdcat.fitness.service

import irdcat.fitness.repository.SummaryRepository
import irdcat.fitness.repository.model.BodyweightParameter
import irdcat.fitness.repository.model.ExerciseSummary
import irdcat.fitness.service.dto.BodyweightSummaryDto
import irdcat.fitness.service.dto.ExerciseDto
import irdcat.fitness.service.dto.ExerciseSummaryDto
import irdcat.fitness.service.dto.ExerciseSummaryParametersDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SummaryService(
    private val summaryRepository: SummaryRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun calculateExerciseSummary(from: LocalDate, to: LocalDate, name: String): Mono<ExerciseSummaryDto> {

        return summaryRepository
            .findExerciseSummaryParametersWithDates(from, to, name)
            .map {
                ExerciseSummaryDto(
                    it.exercise,
                    it.parameters.associate { i ->
                        val mappedParams = if (it.exercise.isBodyweight) {
                            i.parameters
                        } else {
                            i.parameters.copy(bodyweightVolume = null, averageBodyweightVolume = null)
                        }
                        i.date.format(DateTimeFormatter.ISO_DATE) to mappedParams
                    }
                )
            }
            .switchIfEmpty(
                ExerciseSummaryDto(
                    ExerciseDto(name, false),
                    mapOf<String, ExerciseSummaryParametersDto>()
                ).toMono())
            .doOnNext {
                logger.debug(
                    "Exercise Summary: [name={}, dataPoints={}]",
                    it.exercise.name, it.parameters.size)
            }
    }

    fun calculateBodyweightSummary(from: LocalDate, to: LocalDate): Mono<BodyweightSummaryDto> {

        return summaryRepository
            .findBodyweightsWithDates(from, to)
            .map { Pair(it.date.format(DateTimeFormatter.ISO_DATE), it.value.toFloat()) }
            .collectList()
            .map { BodyweightSummaryDto(it.toMap()) }
            .doOnNext { logger.debug("Bodyweight Summary: [dataPoints={}]", it.parameters.size) }
    }
}
