package irdcat.fitness.service.dto

data class CountedExerciseDto(
    val exercise: ExerciseDto,
    val best: Float?,
    val count: Int
)
