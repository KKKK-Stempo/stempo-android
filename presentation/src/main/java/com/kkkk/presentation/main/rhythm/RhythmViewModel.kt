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

    var tempBpm = MutableLiveData<Int>(65)
    var tempBit = MutableLiveData<Int>(2)

    var filename: String = "stempo_bpm_65_bit_2"

    private val _rhythmUrlState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val rhythmUrlState: StateFlow<UiState<String>> = _rhythmUrlState

    private val _downloadWavState = MutableStateFlow<UiState<ByteArray>>(UiState.Empty)
    val downloadWavState: StateFlow<UiState<ByteArray>> = _downloadWavState

    private val _isRecordSaved = MutableSharedFlow<Boolean>()
    val isRecordSaved: SharedFlow<Boolean>
        get() = _isRecordSaved

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    private val _firstStepTime = MutableStateFlow(0L)

    init {
        initRhythmLevelFromDataStore()
    }

    private fun initRhythmLevelFromDataStore() {
        bpm = getBpmFromDataStore()
        // TODO  비트 저장 후 추가
        tempBpm.value = bpm
        tempBit.value = bit
        filename = "stempo_bpm_${bpm}_bit_${bit}"
    }

    fun addStepCount(newStepCount: Int) {
        _stepCount.value += newStepCount
    }

    fun setTempRhythmBpm(bpm: Int) {
        tempBpm.value = bpm
    }

    fun resetTempRhythmLevel() {
//        if (!isSubmitted) {
//            isSubmitted = true
//            tempRhythmLevel.value = rhythmLevel.value
//        }
    }

    fun setRhythmLevel() {
//        isSubmitted = true
//        filename = "stempo_level_" + tempRhythmLevel.value.toString()
//        bpm = setBpm(tempRhythmLevel.value ?: 1)
//        userRepository.setBpm(bpm)
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
                    _firstStepTime.value = System.currentTimeMillis()
                }
                .onFailure {
                    _downloadWavState.value = UiState.Failure(it.message.toString())
                }
        }
    }

    fun posRhythmRecordToSave() {
        var accuracy =
            (stepCount.value.toDouble() / (bpm / 60 * ((System.currentTimeMillis() - _firstStepTime.value) / 10000)))
        if (accuracy > 1) {
            accuracy = max(2 - accuracy, 0.0)
        }

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
        _stepCount.value = 0
        _firstStepTime.value = 0L
    }

    fun getBpmFromDataStore() = userRepository.getBpm()
}