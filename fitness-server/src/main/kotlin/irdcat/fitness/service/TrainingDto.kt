package irdcat.fitness.service

import java.util.Date

data class TrainingDto(
    val date: Date,
    val bodyweight: Float,
    val exercises: List<TrainingExerciseDto>
)
