package irdcat.fitness.model

data class TrainingExercise(
    val order: Int,
    val exerciseId: String,
    val sets: List<TrainingExerciseSet>
)
