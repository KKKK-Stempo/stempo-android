package com.kkkk.presentation.main.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.response.StudyModel.StudyItemModel
import com.kkkk.domain.repository.StudyRepository
import com.kkkk.presentation.main.homework.model.HomeworkMode
import com.kkkk.presentation.manager.AmplitudeManager
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
        AmplitudeManager.trackEvent("homework_mode_change", mapOf("mode" to selectedMode.name))
    }

    fun changeDialogVisible(isDialogVisible: Boolean) {
        _homeworkState.update { it.copy(isDialogVisible = isDialogVisible) }
    }

    private fun changeIsLoading(isLoading: Boolean) {
        _homeworkState.update { it.copy(isLoading = isLoading) }
    }

    private fun updateHomeworkList(homeworkList: List<StudyItemModel>) {
        _homeworkState.update { it.copy(homeworkList = homeworkList.toPersistentList()) }
    }

    private fun getHomeworkList() {
        changeIsLoading(true)
        viewModelScope.launch {
            studyRepository.getHomeworks(0, 1000)
                .onSuccess {
                    updateHomeworkList(it.items)
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
                    updateHomeworkList(
                        homeworkState.value.homeworkList
                            .add(StudyItemModel(id, description, false))
                            .sortedWith(compareBy<StudyItemModel> { it.completed }.thenBy { it.id })
                    )
                    changeDialogVisible(false)
                    AmplitudeManager.trackEvent("add_homework")
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
                    updateHomeworkList(
                        homeworkState.value.homeworkList.filter { it.id != homeworkId }
                    )
                    AmplitudeManager.trackEvent("delete_homework")
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
                    updateHomeworkList(
                        homeworkState.value.homeworkList
                            .map {
                                if (it.id == homeworkId) {
                                    it.copy(
                                        description = description,
                                        completed = completed,
                                    )
                                } else it
                            }
                            .sortedWith(compareBy<StudyItemModel> { it.completed }.thenBy { it.id })
                    )
                    AmplitudeManager.trackEvent("update_homework")
                }.onFailure {
                    _homeworkSideEffect.emit(HomeworkSideEffect.ErrorToast)
                }
        }
    }
}