package irdcat.fitness.service.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class TrainingExercise(

    @field: Id
    val id: String?,

    val order: Int,

    val exercise: Exercise,

    val bodyweight: Float,

    val date: LocalDate,

    val sets: List<TrainingExerciseSet>
)
