package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.ViewModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    init {
        initRhythmFromDataStore()
    }

    private fun initRhythmFromDataStore() {
        _rhythmState.update {
            it.copy(bit = userRepository.getBit(), bpm = userRepository.getBpm())
        }
    }

    fun changeSelectedMode(selectedMode: RhythmMode) {
        _rhythmState.update {
            it.copy(selectedMode = selectedMode)
        }
    }

    fun changeIsPlaying(isPlaying: Boolean) {
        _rhythmState.update {
            it.copy(isPlaying = isPlaying)
        }
    }

    fun showBottomSheet(show: Boolean) {
        _rhythmState.update { it.copy(isBottomSheetVisible = show) }
    }

    fun updateRhythm(bit: Int, bpm: Int) {
        _rhythmState.update { it.copy(bit = bit, bpm = bpm) }
        userRepository.setBpm(bpm)
        userRepository.setBit(bit)
        changeIsPlaying(false)
    }
}