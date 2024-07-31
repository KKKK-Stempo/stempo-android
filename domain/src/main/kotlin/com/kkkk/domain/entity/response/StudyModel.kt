package com.kkkk.domain.entity.response

data class StudyModel(
    val currentPage: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<StudyItemModel>
) {
    data class StudyItemModel(
        val id: Int,
        val title: String,
        val content: String,
        val thumbnailUrl: String,
        val createdAt: String
    )
}
