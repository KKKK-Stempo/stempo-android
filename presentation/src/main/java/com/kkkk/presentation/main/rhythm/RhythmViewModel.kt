package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.onboarding.onbarding.OnboardingViewModel
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
    private val userRepository: UserRepository,
) : ViewModel() {
    var tempRhythmLevel = MutableLiveData<Int>(1)
    var bpm = 50
    var filename: String = "stempo_level_1"
    var isSubmitted: Boolean = true

    private val _rhythmLevel = MutableStateFlow<Int>(LEVEL_UNDEFINED)
    val rhythmLevel: StateFlow<Int> = _rhythmLevel

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmUrlState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

    // TODO: shardFlow로 변경
    private val _recordSaveState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val recordSaveState: StateFlow<UiState<String>> = _recordSaveState

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    private val _speed = MutableStateFlow(0.0)

    private val _lastStepTime = MutableStateFlow(0L)
    val lastStepTime: StateFlow<Long> = _lastStepTime

    init {
        initRhythmLevelFromDataStore()
    }

    private fun initRhythmLevelFromDataStore() {
        val currentLevel = userRepository.getBpmLevel()
        filename = "stempo_level_$currentLevel"
        bpm = setBpm(currentLevel)
        _rhythmLevel.value = currentLevel
        tempRhythmLevel.value = currentLevel
    }

    fun addStepCount(newStepCount: Int) {
        _stepCount.value += newStepCount
    }

    fun setSpeed(newSpeed: Double) {
        _speed.value = newSpeed
    }

    fun setLastStepTime(newLastStepTime: Long) {
        _lastStepTime.value = newLastStepTime
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
        bpm = setBpm(tempRhythmLevel.value ?: 1)
        _rhythmLevel.value = tempRhythmLevel.value ?: 1
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
                RecordRequestModel(
                    _speed.value / (_stepCount.value / OnboardingViewModel.SPEED_CALC_INTERVAL + 1),
                    0,
                    stepCount.value
                )
            ).onSuccess {
                resetStepInfo()
                _recordSaveState.value = UiState.Success(it)
            }.onFailure {
                _recordSaveState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private fun resetStepInfo() {
        _stepCount.value = 0
        _speed.value = 0.0
        _lastStepTime.value = 0L
    }

    private fun setBpm(level: Int) = 40 + level * 10

    companion object {
        const val LEVEL_UNDEFINED = -1
    }
}