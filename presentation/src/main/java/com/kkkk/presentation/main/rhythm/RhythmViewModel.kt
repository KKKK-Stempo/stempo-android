package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    private val rhythmRepository: RhythmRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var tempRhythmLevel = MutableLiveData<Int>(1)
    var filename: String = "stempo_level_1"

    private val _rhythmLevel = MutableStateFlow<Int>(LEVEL_UNDEFINED)
    val rhythmLevel: StateFlow<Int> = _rhythmLevel

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmUrlState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

    init {
        initRhythmLevelFromDataStore()
    }

    private fun initRhythmLevelFromDataStore() {
        filename = "stempo_level_" + userRepository.getBpmLevel().toString()
        _rhythmLevel.value = userRepository.getBpmLevel()
    }

    fun setTempRhythmLevel(level: Int) {
        tempRhythmLevel.value = level
    }

    fun setRhythmLevel() {
        filename = "stempo_level_" + tempRhythmLevel.value.toString()
        _rhythmLevel.value = tempRhythmLevel.value ?: -1
        userRepository.setBpmLevel(rhythmLevel.value)
    }

    fun postToGetRhythmUrlFromServer(level: Int) {
        _rhythmUrlState.value = UiState.Loading
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(40 + level * 10)
                .onSuccess {
                    _rhythmUrlState.value = UiState.Success(it)
                }
                .onFailure {
                    _rhythmUrlState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun getRhythmWavFile(url: String) {
        viewModelScope.launch {
            _downloadWavState.value = UiState.Loading
            rhythmRepository.getRhythmWav(url)
                .onSuccess {
                    _downloadWavState.value = UiState.Success(it)
                }
                .onFailure {
                    _downloadWavState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    companion object {
        const val LEVEL_UNDEFINED = -1
    }
}