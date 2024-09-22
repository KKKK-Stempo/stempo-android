package com.kkkk.data.dto.response

import com.kkkk.domain.entity.response.StudyModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyDto(
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("hasPrevious")
    val hasPrevious: Boolean,
    @SerialName("hasNext")
    val hasNext: Boolean,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("totalItems")
    val totalItems: Int,
    @SerialName("take")
    val take: Int,
    @SerialName("items")
    val items: List<StudyItemDto>,
) {
    @Serializable
    data class StudyItemDto(
        @SerialName("id")
        val id: Int,
        @SerialName("description")
        val description: String,
        @SerialName("completed")
        val completed: Boolean,
    ) {
        fun toModel() = StudyModel.StudyItemModel(
            id = id,
            description = description,
            completed = completed
        )
    }

    fun toModel() = StudyModel(
        currentPage = currentPage,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        items = items.map { it.toModel() }
    )
}
