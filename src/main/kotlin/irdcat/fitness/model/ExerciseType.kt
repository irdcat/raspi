package irdcat.fitness.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("exerciseType")
data class ExerciseType(

    @field:Id
    val id: String,

    val name: String,

    val isBodyWeight: Boolean,
)
