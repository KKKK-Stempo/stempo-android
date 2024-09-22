package com.kkkk.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeworkDto(
    @SerialName("description")
    val description: String,
    @SerialName("completed")
    val completed: Boolean,
)
