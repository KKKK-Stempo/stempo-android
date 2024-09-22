package com.kkkk.data.dto.request

import com.kkkk.domain.entity.request.RhythmRequestModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RhythmRequestDto(
    @SerialName("bpm")
    val bpm: Int,
    @SerialName("bit")
    val bit: Int,
) {
    companion object {
        fun RhythmRequestModel.toDto() = RhythmRequestDto(bpm, bit)
    }
}