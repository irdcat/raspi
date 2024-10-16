package irdcat.fitness.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class ExerciseDTO(
    val id: String,
    val typeId: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: Date,
    val order: Int,
    val bodyWeight: Float,
    val sets: List<ExerciseSet>
) {
    companion object {
        fun fromExercise(exercise: Exercise) : ExerciseDTO {
            return ExerciseDTO(
                exercise.id,
                exercise.typeId,
                exercise.date,
                exercise.order,
                exercise.bodyWeight,
                exercise.sets
            )
        }
    }

    fun toExercise() : Exercise {
        return Exercise(
            id,
            typeId,
            date,
            order,
            bodyWeight,
            sets
        )
    }
}
