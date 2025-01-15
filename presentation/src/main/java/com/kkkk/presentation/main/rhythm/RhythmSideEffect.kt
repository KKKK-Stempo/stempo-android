package com.kkkk.presentation.main.rhythm

sealed class RhythmSideEffect {
    data object ErrorToast : RhythmSideEffect()
    data object SaveSuccessToast : RhythmSideEffect()
}