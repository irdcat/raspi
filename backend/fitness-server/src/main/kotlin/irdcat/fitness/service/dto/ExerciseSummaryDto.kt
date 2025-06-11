package irdcat.fitness.service.dto

data class ExerciseSummaryDto(
    val exercise: ExerciseDto,
    val parameters: Map<String, ExerciseSummaryParametersDto>
)
