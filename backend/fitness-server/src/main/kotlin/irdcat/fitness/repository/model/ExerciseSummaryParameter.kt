package irdcat.fitness.repository.model

import irdcat.fitness.service.dto.ExerciseSummaryParametersDto
import java.time.LocalDate

data class ExerciseSummaryParameter(
    val date: LocalDate,
    val parameters: ExerciseSummaryParametersDto
)
