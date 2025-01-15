package com.kkkk.presentation.main.record

sealed class RecordSideEffect {
    data object ErrorToast : RecordSideEffect()
}