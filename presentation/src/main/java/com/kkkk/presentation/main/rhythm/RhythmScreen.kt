package com.kkkk.presentation.main.rhythm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kkkk.presentation.main.rhythm.component.RhythmBottomSheet
import com.kkkk.presentation.main.rhythm.component.RhythmChip
import com.kkkk.presentation.main.rhythm.component.RhythmModeToggle
import com.kkkk.presentation.main.rhythm.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R

@Composable
fun RhythmRoute(
    viewModel: RhythmViewModel = hiltViewModel(),
) {
    val rhythmState by viewModel.rhythmState.collectAsStateWithLifecycle()

    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rhythmState.lottieResource)
    )

    RhythmScreen(
        rhythmState = rhythmState,
        lottieComposition = lottieComposition,
        onToggleSelected = viewModel::changeSelectedMode,
        onPlayBtnClick = viewModel::changeIsPlaying,
        onStopBtnClick = viewModel::changeIsPlaying,
        onChangeBtnClick = { viewModel.showBottomSheet(true) }
    )

    if (rhythmState.isBottomSheetVisible) {
        RhythmBottomSheet(
            rhythmState = rhythmState,
            onDismissRequest = { viewModel.showBottomSheet(false) },
            onSubmitBtnClick = { bit, bpm ->
                viewModel.updateRhythm(bit, bpm)
                viewModel.showBottomSheet(false)
            }
        )
    }
}

@Composable
internal fun RhythmScreen(
    rhythmState: RhythmState,
    lottieComposition: LottieComposition?,
    onToggleSelected: (RhythmMode) -> Unit = {},
    onWatchBtnClick: () -> Unit = {},
    onPlayBtnClick: () -> Unit = {},
    onStopBtnClick: () -> Unit = {},
    onChangeBtnClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = rhythmState.imageResource),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(bottom = 10.dp)
        )
        Image(
            imageVector = ImageVector.vectorResource(id = if (!rhythmState.isPlaying) R.drawable.ic_play else R.drawable.ic_stop),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 10.dp)
                .clickableWithoutRipple { if (rhythmState.isPlaying) onStopBtnClick() else onPlayBtnClick() }
        )
        if (rhythmState.isPlaying) {
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .scale(1.5f)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.padding(top = 24.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            RhythmModeToggle(
                modifier = Modifier.padding(horizontal = 60.dp),
                selectedMode = rhythmState.selectedMode,
                onToggleSelected = onToggleSelected
            )
            if (rhythmState.selectedMode == RhythmMode.RHYTHM) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_watch),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickableWithoutRipple { onWatchBtnClick() }
                        .padding(6.dp)
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 26.dp, start = 60.dp, end = 60.dp)
                .align(Alignment.CenterHorizontally),
            text = if (rhythmState.selectedMode == RhythmMode.RHYTHM) {
                stringResource(R.string.rhythm_tv_title)
            } else {
                stringResource(R.string.stretch_tv_title)
            },
            textAlign = TextAlign.Center,
            style = StempoTheme.typography.head1
        )

        Spacer(modifier = Modifier.weight(1f))

        if (rhythmState.selectedMode == RhythmMode.RHYTHM) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 34.dp)
                    .clickableWithoutRipple { onChangeBtnClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                RhythmChip(
                    text = stringResource(R.string.rhythm_tv_bit, rhythmState.bit),
                    color = rhythmState.color,
                    isFilled = true
                )
                RhythmChip(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    text = stringResource(R.string.rhythm_tv_bpm, rhythmState.bpm),
                    color = rhythmState.color,
                )
                RhythmChip(
                    text = stringResource(R.string.rhythm_tv_step, rhythmState.stepCount),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Dark)
                .clickableWithoutRipple { onChangeBtnClick() }
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_change),
                contentDescription = null,
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = stringResource(id = R.string.rhythm_btn_change_level),
                style = StempoTheme.typography.head4,
                color = White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmScreenPreview() {
    StempoTheme {
        RhythmScreen(
            rhythmState = RhythmState(),
            lottieComposition = null
        )
    }
}