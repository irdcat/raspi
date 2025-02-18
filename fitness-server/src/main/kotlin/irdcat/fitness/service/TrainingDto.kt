package irdcat.fitness.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape
import java.util.Date

data class TrainingDto(

    @field: JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
    val date: Date,

    val bodyweight: Float,

    val exercises: List<TrainingExerciseDto>
)
