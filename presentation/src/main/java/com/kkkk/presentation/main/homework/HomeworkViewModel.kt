package com.kkkk.presentation.main.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import com.kkkk.presentation.main.homework.model.HomeworkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    private val _homeworkState = MutableStateFlow(HomeworkState())
    val homeworkState = _homeworkState.asStateFlow()

    private val _homeworkSideEffect = MutableSharedFlow<HomeworkSideEffect>()
    val homeworkSideEffect = _homeworkSideEffect.asSharedFlow()

    init {
        getHomeworkList()
    }

    fun changeSelectedMode(selectedMode: HomeworkMode) {
        _homeworkState.update { it.copy(selectedMode = selectedMode) }
    }

    fun changeDialogVisible(isDialogVisible: Boolean) {
        _homeworkState.update { it.copy(isDialogVisible = isDialogVisible) }
    }

    fun changeIsLoading(isLoading: Boolean) {
        _homeworkState.update { it.copy(isLoading = isLoading) }
    }

    private fun getHomeworkList() {
        changeIsLoading(true)
        viewModelScope.launch {
            studyRepository.getHomeworks(0, 1000)
                .onSuccess { studyModel ->
                    _homeworkState.update {
                        it.copy(
                            homeworkList = studyModel.items.toPersistentList(),
                            isListEmpty = studyModel.items.isEmpty()
                        )
                    }
                    changeIsLoading(false)
                }.onFailure {
                    _homeworkSideEffect.emit(HomeworkSideEffect.ErrorToast)
                }
        }
    }

    fun addHomework(description: String) {
        viewModelScope.launch {
            studyRepository.addHomework(description)
                .onSuccess { id ->
                    _homeworkState.update { state ->
                        state.copy(
                            homeworkList = state.homeworkList
                                .add(
                                    StudyModel.StudyItemModel(
                                        id,
                                        description,
                                        false
                                    )
                                )
                                .sortedWith(compareBy<StudyModel.StudyItemModel> { it.completed }.thenBy { it.id })
                                .toPersistentList(),
                            isDialogVisible = false
                        )
                    }
                    _homeworkSideEffect.emit(HomeworkSideEffect.SuccessAddToast)
                }.onFailure {
                    _homeworkSideEffect.emit(HomeworkSideEffect.ErrorToast)
                }
        }
    }

    fun deleteHomework(homeworkId: Int) {
        viewModelScope.launch {
            studyRepository.deleteHomework(homeworkId)
                .onSuccess {
                    _homeworkState.update { state ->
                        state.copy(
                            homeworkList = state.homeworkList
                                .filter { it.id != homeworkId }.toPersistentList()
                        )
                    }
                    _homeworkSideEffect.emit(HomeworkSideEffect.SuccessDeleteToast)
                }.onFailure {
                    _homeworkSideEffect.emit(HomeworkSideEffect.ErrorToast)
                }
        }
    }

    fun updateHomework(homeworkId: Int, description: String, completed: Boolean) {
        viewModelScope.launch {
            studyRepository.updateHomework(homeworkId, description, completed)
                .onSuccess {
                    _homeworkState.update { state ->
                        state.copy(
                            homeworkList = state.homeworkList
                                .map {
                                    if (it.id == homeworkId) it.copy(
                                        description = description,
                                        completed = completed
                                    ) else it
                                }
                                .sortedWith(compareBy<StudyModel.StudyItemModel> { it.completed }.thenBy { it.id })
                                .toPersistentList()
                        )
                    }
                }.onFailure {
                    _homeworkSideEffect.emit(HomeworkSideEffect.ErrorToast)
                }
        }
    }
}