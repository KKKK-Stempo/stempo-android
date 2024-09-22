package com.kkkk.presentation.main.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    // 추후 과제 리스트 관련으로 사용
    private val _studyList = MutableStateFlow<List<StudyModel.StudyItemModel>>(emptyList())
    val studyList: StateFlow<List<StudyModel.StudyItemModel>> = _studyList

    private val _typeIsMe = MutableStateFlow(true)
    val typeIsMe: StateFlow<Boolean> = _typeIsMe

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
                    getHomeworks()
                }.onFailure(Timber::e)
        }
    }

    fun updateHomework(homeworkId: Int, description: String, completed: Boolean) {
        viewModelScope.launch {
            studyRepository.updateHomework(homeworkId, description, completed)
                .onSuccess {
                    getHomeworks()
                }.onFailure(Timber::e)
        }
    }
}
