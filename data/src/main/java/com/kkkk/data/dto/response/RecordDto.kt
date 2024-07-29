package com.kkkk.data.dto.response

import com.kkkk.domain.entity.response.RecordModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecordDto(
    @SerialName("accuracy")
    val accuracy: Double,
    @SerialName("duration")
    val duration: Int,
    @SerialName("steps")
    val steps: Int,
    @SerialName("date")
    val date: String
) {
    fun toModel() = RecordModel(accuracy, duration, steps, date)
}