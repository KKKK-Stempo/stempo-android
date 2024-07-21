package com.kkkk.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("status")
    val status: Int,
    @SerialName("message")
    val message: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("path")
    val path: String,
    @SerialName("data")
    val data: T,
)
