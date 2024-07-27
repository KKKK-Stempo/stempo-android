package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.repository.RhythmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    var currentMedia: String = ""

    private val _rhythmState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmState: StateFlow<UiState<String>> = _rhythmState

    fun changeRhythmLevel(level: Int) {
        rhythmLevel.value = level
        bpm = 40 + level * 10
    }

    fun postToGetRhythmFromServer() {
        viewModelScope.launch {
            rhythmRepository.postToGetRhythm(bpm)
                .onSuccess {
                    currentMedia = it
                    _rhythmState.value = UiState.Success(it)
                }
                .onFailure {
                    _rhythmState.value = UiState.Failure(it.message.toString())
                }
        }
    }
}