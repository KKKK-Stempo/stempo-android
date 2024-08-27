package com.kkkk.stempo.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.UserRepository
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
class HomeViewModel
@Inject
constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<HomeSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<HomeSideEffect>
        get() = _sideEffect.asSharedFlow()


    private var vibrationJob: Job? = null

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

        Log.d("qqqq", "${userRepository.getBpmLevel()} & ${userRepository.getDeviceToken()}")

        vibrationJob = viewModelScope.launch {
            while (true) {
                _sideEffect.emit(HomeSideEffect.Vibrate)
                delay(VIBRATION_INTERVAL)
            }
        }
    }

    private fun stopVibration() {
        vibrationJob?.cancel()
        vibrationJob = null
    }

    companion object {
        const val VIBRATION_INTERVAL = 500L // TODO: Server에서 받아올 예정
        const val VIBRATION_DURATION = 100L
    }
}
