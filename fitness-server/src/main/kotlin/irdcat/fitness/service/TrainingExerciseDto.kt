package irdcat.fitness.service

data class TrainingExerciseDto(
    val id: String,
    val exercise: ExerciseDto,
    val sets: List<TrainingExerciseSetDto>
) {
    companion object {
        fun fromTrainingExercise(trainingExercise: TrainingExercise): TrainingExerciseDto {
            return TrainingExerciseDto(
                id = trainingExercise.id.orEmpty(),
                exercise = ExerciseDto.fromExercise(trainingExercise.exercise),
                sets = trainingExercise.sets.map(TrainingExerciseSetDto::fromTrainingExerciseSet)
            )
        }
    }
}
