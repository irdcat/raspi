package irdcat.fitness.service

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
