package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

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
    private var isSubmitted: Boolean = true

    private val _rhythmLevel = MutableStateFlow<Int>(LEVEL_UNDEFINED)
    val rhythmLevel: StateFlow<Int> = _rhythmLevel

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmUrlState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

    private val _isRecordSaved = MutableSharedFlow<Boolean>()
    val isRecordSaved: SharedFlow<Boolean>
        get() = _isRecordSaved

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    private val _oddStepCount = MutableStateFlow(0)
    private val _oddStepTime = MutableStateFlow(0L)

    private val _evenStepCount = MutableStateFlow(0)
    private val _evenStepTime = MutableStateFlow(0L)

    private val _beforeStepTime = MutableStateFlow(0L)

    init {
        initRhythmLevelFromDataStore()
    }

    private fun initRhythmLevelFromDataStore() {
        bpm = userRepository.getBpm()
        val currentLevel = setBpmLevel(bpm)
        filename = "stempo_level_$currentLevel"
        _rhythmLevel.value = currentLevel
        tempRhythmLevel.value = currentLevel
    }

    fun addStepCount(newStepCount: Int) {
        if ((_oddStepCount.value + _evenStepCount.value) % 2 == 0) {
            _oddStepCount.value += newStepCount
            _oddStepTime.value = System.currentTimeMillis() - _beforeStepTime.value
        } else {
            _evenStepCount.value += newStepCount
            _evenStepTime.value = System.currentTimeMillis() - _beforeStepTime.value
        }
        _stepCount.value += newStepCount
        _beforeStepTime.value = System.currentTimeMillis()
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
        userRepository.setBpm(bpm)
        _rhythmLevel.value = tempRhythmLevel.value ?: 1
    }

    fun postToGetRhythmUrlFromServer() {
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
                    _beforeStepTime.value = System.currentTimeMillis()
                }
                .onFailure {
                    _downloadWavState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun posRhythmRecordToSave() {
        val accuracy = calculateAccuracy(_oddStepTime.value, _evenStepTime.value)

        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(
                    accuracy,
                    0,
                    stepCount.value
                )
            ).onSuccess {
                resetStepInfo()
                _isRecordSaved.emit(true)
            }.onFailure {
                _isRecordSaved.emit(false)
            }
        }
    }

    private fun calculateAccuracy(time1: Long, time2: Long): Double {
        val difference = kotlin.math.abs(time1 - time2)

        return when {
            difference == 0L -> 1.0
            difference >= MAX_ALLOWED_DIFFERENCE -> 0.0
            else -> (1 - difference.toDouble() / MAX_ALLOWED_DIFFERENCE)
        }
    }

    fun posRhythmRecordToSaveWatch(
        accuracy: Double,
    ) {
        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(
                    accuracy,
                    0,
                    stepCount.value
                )
            ).onSuccess {
                resetStepInfo()
                _isRecordSaved.emit(true)
            }.onFailure {
                _isRecordSaved.emit(false)
            }
        }
    }

    private fun resetStepInfo() {
        _oddStepCount.value = 0
        _evenStepCount.value = 0
        _oddStepTime.value = 0L
        _evenStepTime.value = 0L
        _beforeStepTime.value = 0L
    }

    fun getBpmFromDataStore() = userRepository.getBpm()

    private fun setBpm(level: Int) = 40 + level * 10

    private fun setBpmLevel(bpm: Int) =
        when (bpm) {
            in 55..65 -> 2
            in 65..75 -> 3
            in 75..85 -> 4
            in 85..95 -> 5
            in 95..105 -> 6
            in 105..115 -> 7
            in 115..125 -> 8
            in 125..Int.MAX_VALUE -> 9
            else -> 1
        }

    companion object {
        const val LEVEL_UNDEFINED = -1
        const val MAX_ALLOWED_DIFFERENCE = 360000L
    }
}