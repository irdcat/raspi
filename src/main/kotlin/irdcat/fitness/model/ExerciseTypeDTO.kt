package irdcat.fitness.model

data class ExerciseTypeDTO(
    val id: String,
    val name: String,
    val isBodyWeight: Boolean
) {
    companion object {
        fun fromExerciseType(exerciseType: ExerciseType) : ExerciseTypeDTO {
            return ExerciseTypeDTO(
                exerciseType.id,
                exerciseType.name,
                exerciseType.isBodyWeight
            )
        }
    }

    fun toExerciseType() : ExerciseType {
        return ExerciseType(id, name, isBodyWeight)
    }
}
