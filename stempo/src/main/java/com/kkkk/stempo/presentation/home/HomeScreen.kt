package com.kkkk.stempo.presentation.home

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import com.kkkk.stempo.R
import com.kkkk.stempo.presentation.home.HomeViewModel.Companion.VIBRATION_DURATION

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle).collect { sideEffect ->
            when (sideEffect) {
                is HomeSideEffect.Vibrate -> {
                    if (!state.isPlayingMusic) return@collect
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            VIBRATION_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        MusicButton(isPlayingMusic = state.isPlayingMusic) {
            viewModel.controlMusic()
        }
    }

}

@Composable
fun MusicButton(
    isPlayingMusic: Boolean,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.img_rhythm_bg_purple),
            contentDescription = "playing music button",
            modifier = Modifier
                .background(
                    if (isPlayingMusic) Color.Cyan
                    else Color.White
                ),
        )
        Image(
            imageVector = ImageVector.vectorResource(
                id =
                if (isPlayingMusic)
                    R.drawable.ic_stop
                else
                    R.drawable.ic_play
            ),
            contentDescription = "playing music button",
            modifier = Modifier,
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun HomePreview() {
    HomeScreen()
}
