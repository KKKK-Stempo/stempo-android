package com.kkkk.data.dto.response

import com.kkkk.domain.entity.response.VideoModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
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
    val items: List<VideoItemDto>,
) {
    @Serializable
    data class VideoItemDto(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String,
        @SerialName("content")
        val content: String,
        @SerialName("thumbnailUrl")
        val thumbnailUrl: String,
        @SerialName("createdAt")
        val createdAt: String,
    ){
        fun toModel() = VideoModel.VideoItemModel(
            id = id,
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            createdAt = createdAt
        )
    }

    fun toModel() = VideoModel(
        currentPage = currentPage,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        items = items.map { it.toModel() }
    )
}
