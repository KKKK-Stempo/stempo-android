package com.kkkk.data.dto.request

import com.kkkk.domain.entity.request.TokenRequestModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRequestDto(
    @SerialName("userId")
    val userId: Long,
) {
    companion object {
        fun TokenRequestModel.toDto() = TokenRequestDto(userId)
    }
}
