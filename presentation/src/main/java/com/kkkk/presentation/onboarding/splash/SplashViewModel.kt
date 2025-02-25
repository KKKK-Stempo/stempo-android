package com.kkkk.presentation.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.manager.AmplitudeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userState = MutableSharedFlow<Boolean>()
    val userState: SharedFlow<Boolean>
        get() = _userState

    fun loginWIthDeviceTag(deviceTag: String) {
        viewModelScope.launch {
            delay(DELAY_TIME)
            authRepository.login(deviceTag)
                .onSuccess { response ->
                    userRepository.setTokens(response.accessToken, response.refreshToken)
                    userRepository.setDeviceToken(deviceTag)
                    _userState.emit(true)
                }.onFailure { error -> // 401일 때 회원가입으로 이동 하도록 구현 필요
                    _userState.emit(false)
                    AmplitudeManager.trackError("login_error", error)
                }
        }
    }

    companion object {
        private const val DELAY_TIME = 1000L
    }
}
