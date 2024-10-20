package irdcat.fitness.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.Date

data class ExerciseFilterDto(
    val typeId: String? = null,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: Date? = null,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val to: Date? = null
)
