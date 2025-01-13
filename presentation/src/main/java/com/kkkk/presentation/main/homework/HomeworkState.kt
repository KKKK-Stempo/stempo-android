package com.kkkk.presentation.main.homework

import com.kkkk.domain.entity.response.StudyModel.StudyItemModel
import com.kkkk.presentation.main.homework.model.HomeworkMode
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class HomeworkState(
    val selectedMode: HomeworkMode = HomeworkMode.MYSELF,
    val homeworkList: PersistentList<StudyItemModel> = persistentListOf(),
    val isListEmpty: Boolean = true,
) {
    val progress: Float
        get() = homeworkList.count { it.completed } / homeworkList.size.toFloat()
}