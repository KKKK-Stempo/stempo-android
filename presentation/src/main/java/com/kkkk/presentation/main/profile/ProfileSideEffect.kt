package com.kkkk.presentation.main.profile

sealed class ProfileSideEffect {
    data object ErrorToast : ProfileSideEffect()
    data object NotPreparedToast : ProfileSideEffect()
}