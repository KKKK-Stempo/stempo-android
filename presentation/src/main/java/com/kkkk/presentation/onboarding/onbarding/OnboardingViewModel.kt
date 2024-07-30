package com.kkkk.presentation.onboarding.onbarding

import androidx.lifecycle.ViewModel
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.START)
    val state: StateFlow<OnboardingState> = _state

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    private val _speed = MutableStateFlow(0f)

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

    fun setBpmLevel() {
        val level = when (_speed.value / (_stepCount.value / SPEED_CALC_INTERVAL)) {
            in 55f..65f -> 2
            in 65f..75f -> 3
            in 75f..85f -> 4
            in 85f..95f -> 5
            in 95f..105f -> 6
            in 15f..115f -> 7
            in 115f..125f -> 8
            in 125f..Float.MAX_VALUE -> 9
            else -> 1
        }

        userRepository.setBpmLevel(level)
    }

    companion object {
        const val SPEED_CALC_INTERVAL = 10
    }
}
