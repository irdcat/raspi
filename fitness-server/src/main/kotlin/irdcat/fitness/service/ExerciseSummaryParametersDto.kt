package irdcat.fitness.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
data class ExerciseSummaryParametersDto(
    val volume: Float,
    val averageVolume: Float,
    val minIntensity: Float,
    val maxIntensity: Float,
    val averageIntensity: Float,
    val bodyweight: Float,

    val bodyweightVolume: Float? = null,
    val averageBodyweightVolume: Float? = null
)
