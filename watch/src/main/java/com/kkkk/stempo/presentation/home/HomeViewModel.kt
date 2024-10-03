package com.kkkk.stempo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.UserRepository
import com.kkkk.stempo.presentation.WatchActivity.Companion.VIBRATION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<HomeSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<HomeSideEffect>
        get() = _sideEffect.asSharedFlow()

    private var vibrationJob: Job? = null

    private val _stepCount = MutableStateFlow(0)

    private val _oddStepCount = MutableStateFlow(0)
    private val _oddStepTime = MutableStateFlow(0L)

    private val _evenStepCount = MutableStateFlow(0)
    private val _evenStepTime = MutableStateFlow(0L)

    private val _beforeStepTime = MutableStateFlow(0L)

    fun controlMusic() {
        _state.value = _state.value.copy(isPlayingMusic = !_state.value.isPlayingMusic)

        if (_state.value.isPlayingMusic) {
            startVibration()
        } else {
            stopVibration()
        }
    }

    private fun startVibration() {
        if (vibrationJob?.isActive == true) return
        _state.value = _state.value.copy(stepCount = 0)

        vibrationJob = viewModelScope.launch {
            while (true) {
                _sideEffect.emit(HomeSideEffect.Vibrate)
                delay(VIBRATION_INTERVAL)
            }
        }
        _beforeStepTime.value = System.currentTimeMillis()
    }

    private fun stopVibration() {
        vibrationJob?.cancel()
        vibrationJob = null

        val accuracy = calculateAccuracy(
            _oddStepTime.value / _oddStepCount.value,
            _evenStepTime.value / _evenStepCount.value
        )

        viewModelScope.launch {
            _sideEffect.emit(
                HomeSideEffect.EndCount(
                    accuracy
                )
            )
        }

        _state.value = _state.value.copy(stepCount = 0)
    }

    private fun calculateAccuracy(time1: Long, time2: Long): Double {
        val difference = kotlin.math.abs(time1 - time2)

        return (1.0 - difference.toDouble() / (time1 + time2)) * 100
    }

    fun addStep(newStepCount: Int = 1) {
        _stepCount.value += newStepCount

        if (_stepCount.value < 2) {
            _beforeStepTime.value = System.currentTimeMillis()
            return
        }

        if (_stepCount.value % 2 == 0) {
            _oddStepCount.value += newStepCount
            _oddStepTime.value += System.currentTimeMillis() - _beforeStepTime.value
        } else {
            _evenStepCount.value += newStepCount
            _evenStepTime.value += System.currentTimeMillis() - _beforeStepTime.value
        }

        _beforeStepTime.value = System.currentTimeMillis()
    }

    companion object {
        const val VIBRATION_DURATION = 50L
        const val MAX_ALLOWED_DIFFERENCE = 3600L
    }
}
