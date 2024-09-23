package com.kkkk.stempo.presentation.home

sealed class HomeSideEffect {
    data object Vibrate : HomeSideEffect()
    data class EndCount(val accuracy: Double) : HomeSideEffect()
}
