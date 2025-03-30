package irdcat.fitness.service

data class TrainingTemplateExerciseDto(
    val exercise: ExerciseDto,
    val setCount: Int,
    val minReps: Int? = null,
    val maxReps: Int? = null
) {
    companion object {
        fun fromTrainingTemplateExercise(
            trainingTemplateExercise: TrainingTemplateExercise): TrainingTemplateExerciseDto {

            return TrainingTemplateExerciseDto(
                ExerciseDto.fromExercise(trainingTemplateExercise.exercise),
                trainingTemplateExercise.setCount,
                trainingTemplateExercise.minReps,
                trainingTemplateExercise.maxReps
            )
        }
    }

    fun toTrainingTemplateExercise(): TrainingTemplateExercise {
        return TrainingTemplateExercise(
            exercise.toExercise(),
            setCount,
            minReps,
            maxReps
        )
    }
}
