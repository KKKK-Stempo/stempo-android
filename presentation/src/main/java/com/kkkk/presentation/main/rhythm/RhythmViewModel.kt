package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.core.state.UiState
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.entity.request.RhythmRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
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
    private val rhythmRepository: RhythmRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    var bpm = MIN_BPM
    var bit = MAX_BIT
    var filename: String = "stempo_bpm_${bpm}_bit_${bit}"

    var tempBpm = MutableLiveData<Int>(MIN_BPM)
    var tempBit = MutableLiveData<Int>(MIN_BIT)
    var isBpmMinusAvailable = MutableLiveData<Boolean>(false)
    var isBpmPlusAvailable = MutableLiveData<Boolean>(true)

    var isLoading = false

    private val _isStretchView = MutableSharedFlow<Boolean>()
    val isStretchView: SharedFlow<Boolean> = _isStretchView

    private val _isRhythmChanged = MutableSharedFlow<Boolean>()
    val isRhythmChanged: SharedFlow<Boolean> = _isRhythmChanged

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
        bit = userRepository.getBit()
        filename = "stempo_bpm_${bpm}_bit_${bit}"
    }

    fun setTempBpm(bpm: Int) {
        tempBpm.value = bpm
        isBpmMinusAvailable.value = bpm != MIN_BPM
        isBpmPlusAvailable.value = bpm != MAX_BPM
    }

    fun plusTempBpm() {
        if (tempBpm.value == MAX_BPM) return
        tempBpm.value = tempBpm.value?.plus(5)
        isBpmMinusAvailable.value = tempBpm.value != MIN_BPM
        isBpmPlusAvailable.value = tempBpm.value != MAX_BPM
    }

    fun minusTempBpm() {
        if (tempBpm.value == MIN_BPM) return
        tempBpm.value = tempBpm.value?.minus(5)
        isBpmMinusAvailable.value = tempBpm.value != MIN_BPM
        isBpmPlusAvailable.value = tempBpm.value != MAX_BPM
    }

    fun setTempBit(bit: Int) {
        tempBit.value = bit
    }

    fun setRhythmToTemp() {
        tempBpm.value = bpm
        tempBit.value = bit
    }

    fun setTempToRhythm() {
        bpm = tempBpm.value ?: MIN_BPM
        bit = tempBit.value ?: MIN_BIT
        filename = "stempo_bpm_${bpm}_bit_${bit}"
        userRepository.setBpm(bpm)
        userRepository.setBit(bit)
        viewModelScope.launch {
            _isRhythmChanged.emit(true)
        }
    }

    fun resetRhythmChangedState() {
        viewModelScope.launch {
            _isRhythmChanged.emit(false)
        }
    }

    fun navigateToStretchView(isStretch: Boolean) {
        viewModelScope.launch {
            _isStretchView.emit(isStretch)
            _isStretchView.resetReplayCache()
        }
    }

    fun postToGetRhythmUrlFromServer() {
        _rhythmUrlState.value = UiState.Loading
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(RhythmRequestModel(bpm, bit))
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

    companion object {
        const val MIN_BPM = 65
        const val MAX_BPM = 115
        const val MIN_BIT = 2
        const val MAX_BIT = 8

        const val MAX_ALLOWED_DIFFERENCE = 360000L
    }
}