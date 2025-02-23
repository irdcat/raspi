package irdcat.fitness.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape
import java.util.Date

data class TrainingDto(

    @field: JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    val date: Date,

    val bodyweight: Float,

    val exercises: List<TrainingExerciseDto>
) {
    companion object {
        fun fromTrainingExercises(trainingExercises: List<TrainingExercise>): TrainingDto {
            return TrainingDto(
                date = trainingExercises
                    .map(TrainingExercise::date)
                    .first(),
                bodyweight = trainingExercises
                    .map(TrainingExercise::bodyweight)
                    .first(),
                exercises = trainingExercises
                    .map(TrainingExerciseDto::fromTrainingExercise)
                    .sortedBy(TrainingExerciseDto::order)
            )
        }
    }

    fun toTrainingExercises(): List<TrainingExercise> {
        return exercises.map {
            TrainingExercise(
                id = it.id,
                order = it.order,
                exercise = it.exercise.toExercise(),
                bodyweight = bodyweight,
                date = date,
                sets = it.sets.map(TrainingExerciseSetDto::toTrainingExerciseSet)
            )
        }.sortedBy(TrainingExercise::order)
    }
}
