package com.kkkk.presentation.main.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    private var videoPage = 0
    private var articlePage = 0

    fun getVideos() {
        viewModelScope.launch {
            studyRepository.getVideos(
                page = videoPage,
                size = 2
            ).onSuccess {
                videoPage = it.currentPage
                // TODO: DATA 연결
            }.onFailure(Timber::e)
        }
    }

    fun getArticles() {
        viewModelScope.launch {
            studyRepository.getArticles(
                page = articlePage,
                size = 2
            ).onSuccess {
                articlePage = it.currentPage
                // TODO: DATA 연결
            }.onFailure(Timber::e)
        }
    }
}
