package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.entity.request.RecordRequestModel
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
    var bpm = 50
    var filename: String = "stempo_level_1"
    var isSubmitted: Boolean = true

    // TODO : 정확도 만져주세용 ㅎㅎ
    var accuracy: Double = 1.0

    private val _rhythmLevel = MutableStateFlow<Int>(LEVEL_UNDEFINED)
    val rhythmLevel: StateFlow<Int> = _rhythmLevel

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmUrlState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

    private val _recordSaveState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val recordSaveState: StateFlow<UiState<String>> = _recordSaveState

    init {
        initRhythmLevelFromDataStore()
    }

    private fun initRhythmLevelFromDataStore() {
        val currentLevel = userRepository.getBpmLevel()
        filename = "stempo_level_$currentLevel"
        bpm = 40 + currentLevel * 10
        _rhythmLevel.value = currentLevel
        tempRhythmLevel.value = currentLevel
    }

    fun setTempRhythmLevel(level: Int) {
        isSubmitted = false
        tempRhythmLevel.value = level
    }

    fun resetTempRhythmLevel() {
        if (!isSubmitted) {
            isSubmitted = true
            tempRhythmLevel.value = rhythmLevel.value
        }
    }

    fun setRhythmLevel() {
        isSubmitted = true
        filename = "stempo_level_" + tempRhythmLevel.value.toString()
        bpm = 40 + (tempRhythmLevel.value?.times(10) ?: 10)
        _rhythmLevel.value = tempRhythmLevel.value ?: -1
        userRepository.setBpmLevel(rhythmLevel.value)
    }

    fun postToGetRhythmUrlFromServer(level: Int) {
        _rhythmUrlState.value = UiState.Loading
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(bpm)
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

    fun posRhythmRecordToSave() {
        _recordSaveState.value = UiState.Loading
        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(accuracy, 0, 0)
            ).onSuccess {
                _recordSaveState.value = UiState.Success(it)
            }.onFailure {
                _recordSaveState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    companion object {
        const val LEVEL_UNDEFINED = -1
    }
}