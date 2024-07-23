package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    // private val authRepository: AuthRepository,
) : ViewModel() {
    var rhythmLevel: Int = 1

    fun changeRhythmLevel(level: Int) {
        rhythmLevel = level
    }
}