package com.kkkk.presentation.onboarding.onbarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
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

    fun setBpmLevel(deviceTag: String) {
        val bpm = max(_speed.value / (_stepCount.value / SPEED_CALC_INTERVAL), 60.0f)

        viewModelScope.launch {
            authRepository.signup(deviceTag).onSuccess {
                userRepository.setTokens(it.accessToken, it.refreshToken)
                userRepository.setBpm(bpm.toInt())
            }.onFailure(Timber::e)
        }
    }

    companion object {
        const val SPEED_CALC_INTERVAL = 10
    }
}
