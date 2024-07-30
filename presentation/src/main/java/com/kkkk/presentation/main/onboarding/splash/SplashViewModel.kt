package com.kkkk.presentation.main.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _userState = MutableSharedFlow<Boolean>()
    val userState: SharedFlow<Boolean>
        get() = _userState

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
}
