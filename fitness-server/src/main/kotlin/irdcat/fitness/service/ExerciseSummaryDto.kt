package irdcat.fitness.service

data class ExerciseSummaryDto(
    val exercise: ExerciseDto,
    val parameters: Map<String, ExerciseSummaryParametersDto>
)
