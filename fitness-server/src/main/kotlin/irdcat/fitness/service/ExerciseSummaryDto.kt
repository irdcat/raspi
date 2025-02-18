package irdcat.fitness.service

import java.util.Date

data class ExerciseSummaryDto(
    val exercise: ExerciseDto,
    val parameters: Map<String, ExerciseSummaryParametersDto>
)
