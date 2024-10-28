package irdcat.fitness.model

import java.time.LocalDate

data class ExerciseSummaryDto(
    val id: String,
    val parameters: Map<LocalDate, ExerciseParametersDto>
)
