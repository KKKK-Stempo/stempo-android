package com.kkkk.presentation.main.homework

sealed class HomeworkSideEffect {
    data object ErrorToast : HomeworkSideEffect()
}