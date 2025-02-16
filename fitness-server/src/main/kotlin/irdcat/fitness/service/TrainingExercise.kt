package irdcat.fitness.service

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document
data class TrainingExercise(

    @field: Id
    val id: String?,

    val exercise: Exercise,

    val bodyweight: Float,

    val date: Date,

    val sets: List<TrainingExerciseSet>
)
