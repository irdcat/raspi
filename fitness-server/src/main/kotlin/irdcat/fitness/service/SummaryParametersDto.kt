package irdcat.fitness.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
data class SummaryParametersDto(
    val volume: Float?,
    val averageVolume: Float?,
    val bodyweightVolume: Float?,
    val minIntensity: Float?,
    val maxIntensity: Float?,
    val averageIntensity: Float?,
    val bodyweight: Float?
)
