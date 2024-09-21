package com.kkkk.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    @SerialName("deviceTag")
    val deviceTag: String,
    @SerialName("password")
    val password: String = ""
)
