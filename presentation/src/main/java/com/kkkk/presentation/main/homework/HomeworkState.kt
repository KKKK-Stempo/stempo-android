package com.kkkk.presentation.main.homework

import com.kkkk.domain.entity.response.StudyModel.StudyItemModel
import com.kkkk.presentation.main.homework.model.HomeworkMode

data class HomeworkState(
    val currentMode: HomeworkMode = HomeworkMode.MYSELF,
    val homeworkList: List<StudyItemModel> = emptyList(),
    val isListEmpty: Boolean = true,
) {
    val progress: Float
        get() = homeworkList.count { it.completed } / homeworkList.size.toFloat()
}