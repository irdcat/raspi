package irdcat.fitness.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("training")
data class Training(
    @field: Id
    val id: String,
    val date: LocalDate,
    val bodyWeight: Float,
    val exercises: List<TrainingExercise>
)