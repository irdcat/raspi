package irdcat.fitness.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("exercise")
data class Exercise(

    @field: Id
    val id: String,

    val typeId: String,

    val date: Date,

    val order: Int,

    val bodyWeight: Float,

    val sets: List<ExerciseSet>
)
