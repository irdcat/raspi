package irdcat.fitness.service

data class TrainingExerciseDto(
    val id: String,
    val exercise: ExerciseDto,
    val sets: List<TrainingExerciseSetDto>
)
