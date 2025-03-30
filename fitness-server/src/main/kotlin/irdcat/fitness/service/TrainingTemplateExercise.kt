package irdcat.fitness.service

data class TrainingTemplateExercise(
    val exercise: Exercise,
    val setCount: Int,
    val minReps: Int? = null,
    val maxReps: Int? = null
)
