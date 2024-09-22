package com.kkkk.domain.entity.response

data class StudyModel(
    val currentPage: Int = 0,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false,
    val items: List<StudyItemModel> = listOf()
) {
    data class StudyItemModel(
        val id: Int = 0,
        val description: String = "",
        val completed: Boolean = false,
    )
}
