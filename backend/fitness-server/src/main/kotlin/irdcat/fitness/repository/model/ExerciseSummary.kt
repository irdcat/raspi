package irdcat.fitness.repository.model

import irdcat.fitness.service.dto.ExerciseDto

data class ExerciseSummary(
    val exercise: ExerciseDto,
    val parameters: List<ExerciseSummaryParameter>
)
