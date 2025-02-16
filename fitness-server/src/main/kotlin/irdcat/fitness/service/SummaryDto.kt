package irdcat.fitness.service

import java.util.Date

data class SummaryDto(
    val exercise: ExerciseDto?,
    val parameters: Map<Date, SummaryParametersDto>
)
