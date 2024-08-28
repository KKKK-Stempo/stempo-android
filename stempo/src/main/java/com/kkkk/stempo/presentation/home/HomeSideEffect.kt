package com.kkkk.stempo.presentation.home

sealed class HomeSideEffect {
    object Vibrate : HomeSideEffect()
    object CountStep : HomeSideEffect()
}
