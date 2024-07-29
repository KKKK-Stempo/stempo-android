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
    var filename: String = "stempo_level_" + rhythmLevel.value.toString()

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

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
        viewModelScope.launch {
            rhythmRepository.getRhythmWav(currentRhythmUrl)
                .onSuccess {
                    _downloadWavState.value = UiState.Success(it)
                }
                .onFailure {
                    _downloadWavState.value = UiState.Failure(it.message.toString())
                }
        }
    }
}