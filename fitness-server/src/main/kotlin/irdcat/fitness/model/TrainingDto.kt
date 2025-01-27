package irdcat.fitness.model

import java.time.LocalDate
import java.util.*

data class TrainingDto(
    val id: String? = null,
    val templateId: String? = null,
    val date: LocalDate? = null,
    val bodyWeight: Float? = null,
    val exercises: List<TrainingExerciseDto>? = null
) {
    companion object {
        fun fromTraining(training: Training): TrainingDto {
            return TrainingDto(
                training.id,
                training.templateId,
                training.date,
                training.bodyWeight,
                training.exercises.map(TrainingExerciseDto::fromTrainingExercise)
            )
        }
    }

    fun toTraining(): Training {
        val newId = if (id.isNullOrBlank()) {
            UUID.randomUUID().toString()
        } else {
            id
        }
        return toTraining(newId)
    }

    fun toTraining(newId: String) : Training {
        return Training(
            newId,
            templateId,
            date!!,
            bodyWeight!!,
            exercises!!.map { it.toTrainingExercise() }
        )
    }
}
