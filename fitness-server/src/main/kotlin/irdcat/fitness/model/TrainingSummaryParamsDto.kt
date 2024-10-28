package irdcat.fitness.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class TrainingSummaryParamsDto(
    val exerciseIds: List<String>,

    @field: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val from: LocalDate,

    @field: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val to: LocalDate
)
