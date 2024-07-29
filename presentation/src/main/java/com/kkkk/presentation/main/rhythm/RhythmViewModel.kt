package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.repository.RhythmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    private val rhythmRepository: RhythmRepository
) : ViewModel() {
    var rhythmLevel = MutableLiveData<Int>(1)
    var bpm: Int = 50

    var currentRhythmUrl: String = ""
    var filename: String = "stempo_level_" + rhythmLevel.toString()

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<File>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<File>> = _downloadWavState

    fun changeRhythmLevel(level: Int) {
        rhythmLevel.value = level
        bpm = 40 + level * 10
    }

    fun postToGetRhythmUrlFromServer() {
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(bpm)
                .onSuccess {
                    currentRhythmUrl = it
                    _rhythmUrlState.value = UiState.Success(it)
                }
                .onFailure {
                    _rhythmUrlState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getRhythmWavFile() {
        Timber.tag("okhttp").d("@@")
        viewModelScope.launch {
            Timber.tag("okhttp").d("@@@@")
            rhythmRepository.getRhythmWav(currentRhythmUrl)
                .onSuccess {
                    _downloadWavState.value = UiState.Success(it)
                    Timber.tag("okhttp").d("@@@@@@")
                }
                .onFailure {
                    _downloadWavState.value = UiState.Failure(it.message.toString())
                    Timber.tag("okhttp").d("@@@@@@@@ ${it.message.toString()}")
                }
        }
    }
}