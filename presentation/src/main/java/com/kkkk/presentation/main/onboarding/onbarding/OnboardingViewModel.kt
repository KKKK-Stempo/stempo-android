package com.kkkk.presentation.main.onboarding.onbarding

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

    fun setState(newState: OnboardingState) {
        _state.value = newState
    }
}

enum class OnboardingState {
    START,
    MEASURE,
    END,
    DONE
}
