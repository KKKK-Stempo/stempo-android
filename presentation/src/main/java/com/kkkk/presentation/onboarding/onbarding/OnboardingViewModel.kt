package com.kkkk.presentation.onboarding.onbarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.START)
    val state: StateFlow<OnboardingState> = _state

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    private val _speed = MutableStateFlow(0f)
    val speed: StateFlow<Float> = _speed

    private val _lastStepTime = MutableStateFlow(0L)
    val lastStepTime: StateFlow<Long> = _lastStepTime

    fun addStepCount(newStepCount: Int) {
        _stepCount.value += newStepCount
    }

    fun setSpeed(newSpeed: Float) {
        _speed.value = newSpeed
    }

    fun setLastStepTime(newLastStepTime: Long) {
        _lastStepTime.value = newLastStepTime
    }

    fun setState(newState: OnboardingState) {
        _state.value = newState
    }

    companion object {
        const val SPEED_CALC_INTERVAL = 10
    }
}
