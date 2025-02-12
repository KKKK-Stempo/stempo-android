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
    @SerialName("leftFootAverageSpeed")
    val leftFootAverageSpeed: Long,
    @SerialName("rightFootAverageSpeed")
    val rightFootAverageSpeed: Long,
    @SerialName("bit")
    val bit: Int,
    @SerialName("bpm")
    val bpm: Int,
) {
    companion object {
        fun RecordRequestModel.toDto() = RecordRequestDto(
            accuracy,
            duration,
            steps,
            leftFootAverageSpeed,
            rightFootAverageSpeed,
            bit,
            bpm
        )
    }
}
