package irdcat.fitness.service.dto

import irdcat.fitness.service.model.Exercise

data class ExerciseDto(

    val name: String,

    val isBodyweight: Boolean
) {
    companion object {
        fun fromExercise(exercise: Exercise): ExerciseDto {
            return ExerciseDto(exercise.name, exercise.isBodyweight)
        }
    }

    fun toExercise(): Exercise {
        return Exercise(name, isBodyweight)
    }
}
