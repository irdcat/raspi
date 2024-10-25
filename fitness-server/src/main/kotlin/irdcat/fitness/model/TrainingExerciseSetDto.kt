package irdcat.fitness.model

data class TrainingExerciseSetDto(
    val reps: Int?,
    val weight: Float?
) {
    companion object {
        fun fromTrainingExerciseSet(set: TrainingExerciseSet): TrainingExerciseSetDto {
            return TrainingExerciseSetDto(set.reps, set.weight);
        }
    }

    fun toTrainingExerciseSet() : TrainingExerciseSet {
        return TrainingExerciseSet(reps!!, weight!!)
    }
}
