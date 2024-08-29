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
import kotlin.math.max

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

    private val _speed = MutableStateFlow(0.0)

    private val _firstStepTime = MutableStateFlow(0L)

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
        _firstStepTime.value = System.currentTimeMillis()
    }

    fun resetStepInfo() {
        _speed.value = 0.0
    }

    private fun stopVibration() {
        vibrationJob?.cancel()
        vibrationJob = null

        var accuracy =
            (_state.value.stepCount.toDouble() / (VIBRATION_INTERVAL / 60 * ((System.currentTimeMillis() - _firstStepTime.value) / 10000)))
        if (accuracy > 1) {
            accuracy = max(2 - accuracy, 0.0)
        }

        viewModelScope.launch {
            _sideEffect.emit(
                HomeSideEffect.EndCount(
                    accuracy
                )
            )
        }

        _state.value = _state.value.copy(stepCount = 0)
    }

    fun addStep() {
        _state.value = _state.value.copy(stepCount = _state.value.stepCount + 1)
    }

    companion object {
        const val VIBRATION_DURATION = 50L
    }
}
