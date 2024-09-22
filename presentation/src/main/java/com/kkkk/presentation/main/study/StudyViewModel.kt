package com.kkkk.presentation.main.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    private val _studyList = MutableStateFlow<List<StudyModel.StudyItemModel>>(emptyList())
    val studyList: StateFlow<List<StudyModel.StudyItemModel>> = _studyList

    private val _typeIsMe = MutableStateFlow(true)
    val typeIsMe: StateFlow<Boolean> = _typeIsMe

    private val _toast = MutableSharedFlow<String>()
    val toast: SharedFlow<String> = _toast

    init {
        getHomeworks()
    }

    private fun getHomeworks() {
        viewModelScope.launch {
            studyRepository.getHomeworks(0, 1000).onSuccess {
                _studyList.value = it.items
            }.onFailure(Timber::e)
        }
    }

    fun setTypeIsMe(isMe: Boolean) {
        _typeIsMe.value = isMe
    }

    fun deleteHomework(homeworkId: Int) {
        viewModelScope.launch {
            studyRepository.deleteHomework(homeworkId)
                .onSuccess {
                    _toast.emit("삭제되었습니다")
                    _studyList.value = _studyList.value.filter { it.id != homeworkId }
                }.onFailure(Timber::e)
        }
    }

    fun updateHomework(homeworkId: Int, description: String, completed: Boolean) {
        viewModelScope.launch {
            studyRepository.updateHomework(homeworkId, description, completed)
                .onSuccess {
                    _toast.emit("수정되었습니다")
                    _studyList.value = _studyList.value.map {
                        if (it.id == homeworkId) {
                            it.copy(description = description, completed = completed)
                        } else {
                            it
                        }
                    }.sortedBy { it.completed }
                }.onFailure(Timber::e)
        }
    }
}
