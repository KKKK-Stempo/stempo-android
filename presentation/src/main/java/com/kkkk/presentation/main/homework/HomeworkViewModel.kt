package com.kkkk.presentation.main.homework

import androidx.lifecycle.ViewModel
import com.kkkk.domain.repository.StudyRepository
import com.kkkk.presentation.main.homework.model.HomeworkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    private val _homeworkState = MutableStateFlow(HomeworkState())
    val homeworkState = _homeworkState.asStateFlow()

    private val _homeworkSideEffect = MutableSharedFlow<HomeworkSideEffect>()
    val homeworkSideEffect = _homeworkSideEffect.asSharedFlow()

    fun changeSelectedMode(selectedMode: HomeworkMode) {
        _homeworkState.update { it.copy(selectedMode = selectedMode) }
    }

}