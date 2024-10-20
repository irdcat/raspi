package irdcat.fitness.model

import java.util.UUID

data class ExerciseTypeDto(
    val id: String,
    val name: String,
    val isBodyWeight: Boolean
) {
    companion object {
        fun fromExerciseType(exerciseType: ExerciseType) : ExerciseTypeDto {
            return ExerciseTypeDto(
                exerciseType.id,
                exerciseType.name,
                exerciseType.isBodyWeight
            )
        }
    }

    fun toExerciseType() : ExerciseType {
        return ExerciseType(id.ifBlank { UUID.randomUUID().toString() }, name, isBodyWeight)
    }
}
