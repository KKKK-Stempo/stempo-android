package com.kkkk.presentation.main.rhythm

import androidx.compose.ui.graphics.Color
import com.kkkk.presentation.main.theme.Green50
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.Sky50
import com.kkkk.stempo.presentation.R

data class RhythmState(
    val selectedMode: RhythmMode = RhythmMode.RHYTHM,
    val isPlaying: PlayState = PlayState.DEFAULT,
    val isLoading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val isDialogVisible: Boolean = false,
    val bit: Int = MIN_BIT,
    val bpm: Int = MIN_BPM,
    val isPlayerLoaded: Boolean = true,
    val stepCount: Int = 0,
    val rhythmWav: ByteArray? = null,
    val beatSound: Int = 0,
    val beatStream: Int = 0,
) {
    val filename: String
        get() = "stempo_bpm_${bpm}_bit_${bit}"

    val color: Color
        get() = when (bit) {
            2 -> Purple50
            3 -> Sky50
            4 -> Green50
            6 -> Purple50
            else -> Sky50
        }

    val lottieResource: Int
        get() = when (color) {
            Purple50 -> R.raw.stempo_rhythm_purple
            Sky50 -> R.raw.stempo_rhythm_sky
            Green50 -> R.raw.stempo_rhythm_green
            else -> R.raw.stempo_rhythm_purple
        }

    val imageResource: Int
        get() = when (color) {
            Purple50 -> R.drawable.img_rhythm_bg_purple
            Sky50 -> R.drawable.img_rhythm_bg_sky
            Green50 -> R.drawable.img_rhythm_bg_green
            else -> R.drawable.img_rhythm_bg_purple
        }


    companion object {
        const val MIN_BPM = 60
        const val MAX_BPM = 120
        const val MIN_BIT = 2
        const val MAX_BIT = 8
    }
}