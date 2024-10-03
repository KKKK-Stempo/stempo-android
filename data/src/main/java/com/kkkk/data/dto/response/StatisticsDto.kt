package com.kkkk.data.dto.response

import com.kkkk.domain.entity.response.StatisticsModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    @SerialName("todayWalkTrainingCount")
    val todayWalkTrainingCount: Int,
    @SerialName("weeklyWalkTrainingCount")
    val weeklyWalkTrainingCount: Int,
    @SerialName("consecutiveWalkTrainingDays")
    val consecutiveWalkTrainingDays: Int,
) {
    fun toModel() = StatisticsModel(
        todayWalkTrainingCount = todayWalkTrainingCount,
        weeklyWalkTrainingCount = weeklyWalkTrainingCount,
        consecutiveWalkTrainingDays = consecutiveWalkTrainingDays
    )
}
