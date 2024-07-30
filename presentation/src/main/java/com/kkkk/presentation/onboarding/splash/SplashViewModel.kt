package com.kkkk.presentation.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _isValidToken = MutableSharedFlow<Boolean>(replay = 1)
    val isValidToken: SharedFlow<Boolean>
        get() = _isValidToken

    private val _userState = MutableSharedFlow<Boolean>()
    val userState: SharedFlow<Boolean>
        get() = _userState

    fun checkTokenState() {
        viewModelScope.launch {
            delay(DELAY_TIME)
            _isValidToken.emit(userRepository.getAccessToken().isNotBlank())
        }
    }

    fun setAndroidId(deviceTag: String) {
        viewModelScope.launch {
            authRepository.login(deviceTag)
                .onSuccess { response ->
                    userRepository.setTokens(response.accessToken, response.refreshToken)
                    userRepository.setDeviceToken(deviceTag)
                    _userState.emit(true)
                }.onFailure(Timber::e)
        }
    }

    companion object {
        private const val DELAY_TIME = 1000L
    }
}
