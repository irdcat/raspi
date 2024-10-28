package irdcat.fitness.model

data class ExerciseParametersDto (
    val volume: Float,
    val averageVolume: Float,
    val averageIntensity: Float,
    val minIntensity: Float,
    val maxIntensity: Float
) {
    companion object {
        fun fromTrainingExercise(exercise: TrainingExercise): ExerciseParametersDto {
            val sumVol = exercise.sets
                .map { it.reps * it.weight }
                .sum()
            val avgVol = sumVol / exercise.sets.size
            val sumInt = exercise.sets
                .map { it.weight }
                .sum()
            val avgInt = sumInt / exercise.sets.size
            val minInt = exercise.sets.maxOf { it.weight }
            val maxInt = exercise.sets.maxOf { it.weight }

            return ExerciseParametersDto(sumVol, avgVol, avgInt, minInt, maxInt)
        }
    }
}