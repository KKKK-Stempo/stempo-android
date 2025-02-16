package com.kkkk.presentation.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.main.profile.model.ProfileButtonType
import com.kkkk.presentation.manager.AmplitudeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    private val _profileSideEffect = MutableSharedFlow<ProfileSideEffect>()
    val profileSideEffect = _profileSideEffect.asSharedFlow()

    fun startWebsiteWithUrl(btnType: ProfileButtonType) {
        viewModelScope.launch {
            _profileSideEffect.emit(ProfileSideEffect.WebsiteClicked(btnType.url))
        }
        AmplitudeManager.trackEvent("click_website", mapOf("website" to btnType.name))
    }

    fun showNotPreparedToast() {
        viewModelScope.launch {
            _profileSideEffect.emit(ProfileSideEffect.NotPreparedToast)
        }
    }

    fun updateIsDialogVisible(isVisible: Boolean) {
        _profileState.value = _profileState.value.copy(isDialogVisible = isVisible)
    }

    fun withdrawAccount() {
        viewModelScope.launch {
            authRepository.unregister(
                authorization = BEARER + " " + userRepository.getRefreshToken()
            ).onSuccess {
                userRepository.clearInfo()
                AmplitudeManager.trackEvent("withdraw_account")
                _profileSideEffect.emit(ProfileSideEffect.ProfileCleared)
            }
        }
    }

    companion object {
        private const val BEARER = "Bearer"
    }
}