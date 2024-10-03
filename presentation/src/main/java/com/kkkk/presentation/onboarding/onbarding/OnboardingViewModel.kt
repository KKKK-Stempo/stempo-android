package com.kkkk.presentation.onboarding.onbarding

import android.util.Log
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
import kotlin.math.round

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.START)
    val state: StateFlow<OnboardingState> = _state

    private val _stepCount = MutableStateFlow(0)

    fun addStepCount(newStepCount: Int) {
        _stepCount.value += newStepCount
    }

    fun setState(newState: OnboardingState) {
        _state.value = newState
    }

    fun setBpmLevel(deviceTag: String) {
        val bpm = round(_stepCount.value.coerceIn(60, 120) / 10.0) * 10

        viewModelScope.launch {
            authRepository.signup(deviceTag).onSuccess {
                userRepository.setTokens(it.accessToken, it.refreshToken)
                userRepository.setBpm(bpm.toInt())
            }.onFailure(Timber::e)
        }
    }
}
