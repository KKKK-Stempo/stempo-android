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

    fun getVideos() {
        viewModelScope.launch {
            studyRepository.getVideos(
                page = 0,
                size = 2
            ).onSuccess {
                // TODO: DATA 연결
            }.onFailure(Timber::e)
        }
    }
}
