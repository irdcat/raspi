package irdcat.fitness.service

data class TrainingTemplateExerciseDto(
    val exercise: ExerciseDto,
    val setCount: Int
) {
    companion object {
        fun fromTrainingTemplateExercise(
            trainingTemplateExercise: TrainingTemplateExercise): TrainingTemplateExerciseDto {

            return TrainingTemplateExerciseDto(
                ExerciseDto.fromExercise(trainingTemplateExercise.exercise),
                trainingTemplateExercise.setCount
            )
        }
    }

    fun toTrainingTemplateExercise(): TrainingTemplateExercise {
        return TrainingTemplateExercise(
            exercise.toExercise(),
            setCount
        )
    }
}
