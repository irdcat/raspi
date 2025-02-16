package irdcat.fitness.service

data class CountedExerciseDto(
    val exercise: ExerciseDto,
    val count: Int
)
