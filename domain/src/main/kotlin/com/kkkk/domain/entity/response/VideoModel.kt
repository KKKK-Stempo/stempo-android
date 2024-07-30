package com.kkkk.domain.entity.response

data class VideoModel(
    val currentPage: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<VideoItemModel>
) {
    data class VideoItemModel(
        val id: Int,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val createdAt: String
    )
}
