package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.request.RhythmRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    private val rhythmRepository: RhythmRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _rhythmState = MutableStateFlow(RhythmState())
    val rhythmState = _rhythmState.asStateFlow()

    private val _rhythmSideEffect = MutableSharedFlow<RhythmSideEffect>()
    val rhythmSideEffect = _rhythmSideEffect.asSharedFlow()

    init {
        initRhythmFromDataStore()
    }

    private fun initRhythmFromDataStore() {
        _rhythmState.update {
            it.copy(bit = userRepository.getBit(), bpm = userRepository.getBpm())
        }
    }

    fun changeSelectedMode(selectedMode: RhythmMode) {
        _rhythmState.update { it.copy(selectedMode = selectedMode, isPlaying = PlayState.DEFAULT) }
    }

    fun changeIsPlaying(isPlaying: PlayState) {
        _rhythmState.update { it.copy(isPlaying = isPlaying) }
    }

    fun changeIsLoading(isLoading: Boolean) {
        _rhythmState.update { it.copy(isLoading = isLoading) }
    }

    fun showBottomSheet(show: Boolean) {
        _rhythmState.update { it.copy(isBottomSheetVisible = show, isPlaying = PlayState.DEFAULT) }
    }

    fun showDialog(show: Boolean) {
        _rhythmState.update { it.copy(isDialogVisible = show) }
    }

    fun updateRhythm(bit: Int, bpm: Int) {
        _rhythmState.update { it.copy(bit = bit, bpm = bpm, isPlaying = PlayState.DEFAULT) }
        userRepository.setBpm(bpm)
        userRepository.setBit(bit)
    }

    fun updateIsPlayerLoaded(isPlayerLoaded: Boolean) {
        _rhythmState.update { it.copy(isPlayerLoaded = isPlayerLoaded) }
    }

    fun updateBeatStream(beatStream: Int) {
        _rhythmState.update { it.copy(beatStream = beatStream) }
    }

    fun updateBeatSound(beatSound: Int) {
        _rhythmState.update { it.copy(beatSound = beatSound) }
    }

    fun playMusic() {
        viewModelScope.launch {
            if (rhythmState.value.isPlayerLoaded) {
                _rhythmState.update { it.copy(isPlaying = PlayState.PLAYING) }
            } else {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    fun pauseMusic() {

    }

    fun getRhythmUrlState() {
        changeIsLoading(true)
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(
                RhythmRequestModel(
                    rhythmState.value.bpm,
                    rhythmState.value.bit
                )
            )
                .onSuccess {
                    getRhythmWavFile(it)
                }
                .onFailure {
                    _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
                }
        }
    }

    private fun getRhythmWavFile(url: String) {
        viewModelScope.launch {
            rhythmRepository.getRhythmWav(url)
                .onSuccess { wav ->
                    _rhythmState.update { it.copy(rhythmWav = wav) }
                }
                .onFailure {
                    _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
                }
        }
    }

    fun postRhythmRecordToSave() {
        viewModelScope.launch {
//            rhythmRepository.postRhythmRecord(
//                RecordRequestModel(
//                    accuracy,
//                    0,
//                    stepCount.value
//                )
//            ).onSuccess {
//                resetStepInfo()
//                _isRecordSaved.emit(true)
//            }.onFailure {
//                _isRecordSaved.emit(false)
//            }
            _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
        }
    }

    companion object {
        const val FLOAT_80 = 80.00000000000000000000F
    }
}