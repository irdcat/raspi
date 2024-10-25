package irdcat.fitness.model

import java.util.UUID

data class ExerciseDto(
    val id: String? = null,
    val name: String? = null,
    val isBodyWeight: Boolean? = null
) {
    companion object {
        fun fromExercise(exercise: Exercise) : ExerciseDto {
            return ExerciseDto(
                exercise.id,
                exercise.name,
                exercise.isBodyWeight
            )
        }
    }

    fun toExercise() : Exercise {
        val newId = if (id.isNullOrBlank()) {
            UUID.randomUUID().toString()
        } else {
            id
        }
        return toExercise(newId)
    }

    fun toExercise(newId: String) : Exercise {
        return Exercise(newId, name!!, isBodyWeight!!)
    }
}
