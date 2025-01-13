package com.kkkk.presentation.main.homework

sealed class HomeworkSideEffect {
    data object ErrorToast : HomeworkSideEffect()
    data object SuccessAddToast : HomeworkSideEffect()
    data object SuccessDeleteToast : HomeworkSideEffect()
}