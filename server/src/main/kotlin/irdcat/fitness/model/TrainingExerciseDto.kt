package irdcat.fitness.model

data class TrainingExerciseDto(
    val order: Int?,
    val exerciseId: String?,
    val sets: List<TrainingExerciseSetDto>?
) {
    companion object {
        fun fromTrainingExercise(trainingExercise: TrainingExercise): TrainingExerciseDto {
            return TrainingExerciseDto(
                trainingExercise.order,
                trainingExercise.exerciseId,
                trainingExercise.sets
                    .map(TrainingExerciseSetDto::fromTrainingExerciseSet)
            )
        }
    }

    fun toTrainingExercise(): TrainingExercise {
        return TrainingExercise(
            order!!,
            exerciseId!!,
            sets!!.map { it.toTrainingExerciseSet() }
        )
    }
}
