package irdcat.fitness.service.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class TrainingTemplate(
    @field: Id
    val id: String?,

    val name: String,

    val group: String,

    val description: String,

    val exercises: List<TrainingTemplateExercise>
)
