package irdcat.fitness.model

import java.util.*

data class TrainingTemplateDto(
    val id: String? = null,
    val name: String? = null,
    val groupName: String? = null,
    val description: String? = null,
    val exerciseIds: List<String>? = null
) {
    companion object {
        fun fromTrainingTemplate(trainingTemplate: TrainingTemplate): TrainingTemplateDto {
            return TrainingTemplateDto(
                trainingTemplate.id,
                trainingTemplate.name,
                trainingTemplate.groupName,
                trainingTemplate.description,
                trainingTemplate.exerciseIds
            )
        }
    }

    fun toTrainingTemplate(): TrainingTemplate {
        val newId = if(id.isNullOrBlank()) {
            UUID.randomUUID().toString()
        } else {
            id
        }
        return toTrainingTemplate(newId)
    }

    fun toTrainingTemplate(newId: String): TrainingTemplate {
        return TrainingTemplate(
            newId,
            name!!,
            groupName!!,
            description!!,
            exerciseIds!!
        )
    }
}
