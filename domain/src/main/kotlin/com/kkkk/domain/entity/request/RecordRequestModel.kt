package com.kkkk.domain.entity.request

data class RecordRequestModel(
    val accuracy: Double,
    val duration: Int = 0,
    val steps: Int,
    val leftFootAverageSpeed: Long,
    val rightFootAverageSpeed: Long,
    val bit: Int,
    val bpm: Int,
)
