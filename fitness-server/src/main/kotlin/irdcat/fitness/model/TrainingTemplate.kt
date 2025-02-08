package irdcat.fitness.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("template")
data class TrainingTemplate(
    @field: Id
    val id: String,
    val name: String,
    val groupName: String,
    val description: String,
    val exerciseIds: List<String>
)
