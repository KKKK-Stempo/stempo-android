package com.kkkk.data.dto.request

import com.kkkk.domain.entity.request.RecordRequestModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecordRequestDto(
    @SerialName("accuracy")
    val accuracy: Double,
    @SerialName("duration")
    val duration: Int,
    @SerialName("steps")
    val steps: Int,
) {
    companion object {
        fun RecordRequestModel.toDto() = RecordRequestDto(accuracy, duration, steps)
    }
}