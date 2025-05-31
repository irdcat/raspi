package irdcat.fitness.service.dto

import irdcat.fitness.service.model.TrainingExerciseSet

data class TrainingExerciseSetDto(
    val repetitions: Int,
    val weight: Float
) {
    companion object {
        fun fromTrainingExerciseSet(trainingExerciseSet: TrainingExerciseSet): TrainingExerciseSetDto {
            return TrainingExerciseSetDto(trainingExerciseSet.repetitions, trainingExerciseSet.weight)
        }
    }

    fun toTrainingExerciseSet(): TrainingExerciseSet {
        return TrainingExerciseSet(repetitions, weight)
    }
}
