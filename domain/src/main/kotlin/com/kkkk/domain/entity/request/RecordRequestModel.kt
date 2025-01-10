package com.kkkk.domain.entity.request

data class RecordRequestModel(
    val accuracy: Double,
    val duration: Int = 0,
    val steps: Int,
)