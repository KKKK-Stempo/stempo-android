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
    private val _videoState = MutableStateFlow(StudyModel())
    val videoState: StateFlow<StudyModel>
        get() = _videoState

    private val _articleState = MutableStateFlow(StudyModel())
    val articleState: StateFlow<StudyModel>
        get() = _articleState

    init {
        getVideos()
        getArticles()
    }

    fun getVideos(value: Int = 0) {
        viewModelScope.launch {
            studyRepository.getVideos(
                page = _videoState.value.currentPage + value,
                size = 2
            ).onSuccess {
                _videoState.value = it
            }.onFailure(Timber::e)
        }
    }

    fun getArticles(value: Int = 0) {
        viewModelScope.launch {
            studyRepository.getArticles(
                page = _articleState.value.currentPage + value,
                size = 3
            ).onSuccess {
                _articleState.value = it
            }.onFailure(Timber::e)
        }
    }
}
