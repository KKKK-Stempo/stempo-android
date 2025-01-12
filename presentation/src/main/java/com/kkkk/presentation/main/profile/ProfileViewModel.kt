package com.kkkk.presentation.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.main.profile.model.ProfileButtonType
import com.kkkk.presentation.xmlmain.xmlprofile.XmlProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun updateWebsiteUrl(url: String) {
        _profileState.value = _profileState.value.copy(clickedWebsiteUrl = url)
    }

    fun startWebsiteWithUrl(btnType: ProfileButtonType) {
        when (btnType) {
            ProfileButtonType.BTN_ANNOUNCE -> updateWebsiteUrl(URL_ANNOUNCE)
            ProfileButtonType.BTN_FAQ -> updateWebsiteUrl(URL_FAQ)
            ProfileButtonType.BTN_SUGGEST -> updateWebsiteUrl(URL_SUGGEST)
            ProfileButtonType.DEFAULT -> updateWebsiteUrl("")
        }
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
                _profileState.update { it.copy(isProfileCleared = true) }
            }
        }
    }

    companion object {
        private const val BEARER = "Bearer"

        private const val URL_ANNOUNCE =
            "https://field-colt-189.notion.site/Stempo-e8252a5094eb4351819809dc3abd6623?pvs=4"
        private const val URL_FAQ =
            "https://field-colt-189.notion.site/FAQ-3e01b4b00b0c4b2c84aaacd79b2b6045?pvs=4"
        private const val URL_SUGGEST =
            "https://docs.google.com/forms/d/e/1FAIpQLSfTyRUJzIURmSvIJOgJlqqLCRECVPtTHWj8xCNsBrIIuzwBRA/viewform"
    }
}