package com.kkkk.data.dto.response

import com.kkkk.domain.entity.response.RecordList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecordListDto(
    @SerialName("accuracyAverage")
    val accuracyAverage: Int,
    @SerialName("records")
    val records: List<RecordDto>,
) {
    fun toModel() = RecordList(accuracyAverage, records.map { it.toModel() })
}
