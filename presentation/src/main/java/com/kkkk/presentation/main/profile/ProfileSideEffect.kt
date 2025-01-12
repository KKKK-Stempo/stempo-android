package com.kkkk.presentation.main.profile

sealed class ProfileSideEffect {
    data object ErrorToast : ProfileSideEffect()
    data object NotPreparedToast : ProfileSideEffect()
    data object ProfileCleared : ProfileSideEffect()
    data class WebsiteClicked(val url: String) : ProfileSideEffect()
}