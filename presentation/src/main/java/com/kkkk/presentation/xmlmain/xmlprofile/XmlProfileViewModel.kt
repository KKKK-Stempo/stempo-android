package com.kkkk.presentation.xmlmain.xmlprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class XmlProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _rebirth = MutableSharedFlow<Unit>()
    val rebirth: SharedFlow<Unit> = _rebirth.asSharedFlow()

    fun withdraw() {
        viewModelScope.launch {
            authRepository.unregister(
                authorization = BEARER + " " + userRepository.getRefreshToken()
            ).onSuccess {
                userRepository.clearInfo()
                _rebirth.emit(Unit)
            }
        }
    }

    companion object {
        private const val BEARER = "Bearer"
    }
}
