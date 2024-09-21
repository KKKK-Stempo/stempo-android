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
    var bpm = 65
    var bit = 2
    var filename: String = "stempo_bpm_65_bit_2"

    var tempBpm = MutableLiveData<Int>(65)
    var tempBit = MutableLiveData<Int>(2)

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
        bpm = getBpmFromDataStore()
        // TODO  비트 저장 후 추가
        filename = "stempo_bpm_${bpm}_bit_${bit}"
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

    fun setTempBpm(bpm: Int) {
        tempBpm.value = bpm
    }

    fun setRhythmToTemp() {
        tempBpm.value = bpm
        tempBit.value = bit
    }

    fun setTempToRhythm() {
        bpm = tempBpm.value ?: 65
        bit = tempBit.value ?: 2
        filename = "stempo_bpm_${bpm}_bit_${bit}"
        userRepository.setBpm(bpm)
        // TODO  비트 저장
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

    companion object {
        const val MAX_ALLOWED_DIFFERENCE = 360000L
    }
}