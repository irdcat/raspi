package irdcat.fitness.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("exercise")
data class Exercise(

    @field:Id
    val id: String,

    val name: String,

    val isBodyWeight: Boolean,
)
