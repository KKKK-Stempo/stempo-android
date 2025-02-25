package com.kkkk.presentation.main.rhythm

import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_DETECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.component.OneButtonDialog
import com.kkkk.presentation.main.component.TwoButtonDialog
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.KEY_RECORD
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_END
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_RECORD
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_START
import com.kkkk.presentation.main.rhythm.component.RhythmBottomSheet
import com.kkkk.presentation.main.rhythm.component.RhythmChip
import com.kkkk.presentation.main.rhythm.component.RhythmModeToggle
import com.kkkk.presentation.main.rhythm.model.PlayState
import com.kkkk.presentation.main.rhythm.model.RhythmMode
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent50
import com.kkkk.presentation.main.theme.White
import com.kkkk.presentation.manager.AmplitudeManager
import com.kkkk.stempo.presentation.R
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhythmRoute(
    viewModel: RhythmViewModel = hiltViewModel(),
) {
    val rhythmState by viewModel.rhythmState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // 시스템 서비스 및 데이터 클라이언트
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepDetectorSensor = sensorManager.getDefaultSensor(TYPE_STEP_DETECTOR)
    val wearableDataClient = Wearable.getDataClient(context)

    // Lottie 및 애니메이션 속도 관리
    val lottiePlaying by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rhythmState.lottieResource)
    )
    val lottieLoading by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.stempo_loading)
    )

    // 바텀시트 관련 상태
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.9f

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            AmplitudeManager.trackEvent("view_rhythm")
        }
    }

    LaunchedEffect(viewModel.rhythmSideEffect, lifecycleOwner) {
        viewModel.rhythmSideEffect.collect { sideEffect ->
            when (sideEffect) {
                RhythmSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
                RhythmSideEffect.SaveSuccessToast -> context.toast(context.stringOf(R.string.rhythm_toast_save_success))
            }
        }
    }

    LaunchedEffect(rhythmState.isLoading) {
        systemUiController.setStatusBarColor(color = if (rhythmState.isLoading) Transparent50 else White)
    }

    LaunchedEffect(rhythmState.bit, rhythmState.bpm, rhythmState.selectedMode) {
        if (!File(context.filesDir, rhythmState.filename).exists()) {
            viewModel.downloadNewMusicFile(File(context.filesDir, rhythmState.filename).toPath())
        } else {
            viewModel.updateIsPlayerLoaded(false)
        }
    }

    LaunchedEffect(rhythmState.isPlayerLoaded) {
        if (!rhythmState.isPlayerLoaded) viewModel.loadMusicPlayers()
    }

    LaunchedEffect(rhythmState.isPlaying) {
        when (rhythmState.isPlaying) {
            PlayState.PLAYING -> viewModel.playMusic()
            PlayState.PAUSE -> viewModel.pauseMusic(true)
            PlayState.STOP -> viewModel.recordCurrentStepAccuracy()
            PlayState.DEFAULT -> viewModel.pauseMusic(false)
        }
    }

    DisposableEffect(sensorManager) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == TYPE_STEP_DETECTOR) viewModel.addStepCount()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(sensorListener, stepDetectorSensor, SENSOR_DELAY_NORMAL)
        onDispose { sensorManager.unregisterListener(sensorListener) }
    }

    DisposableEffect(wearableDataClient) {
        val wearableDataListener = DataClient.OnDataChangedListener { dataEvents ->
            dataEvents.filter { it.type == DataEvent.TYPE_CHANGED }.map { it.dataItem }
                .forEach { item ->
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    when (item.uri.path) {
                        PATH_START -> viewModel.changeIsPlaying(PlayState.PLAYING)
                        PATH_END -> viewModel.changeIsPlaying(PlayState.DEFAULT)
                        PATH_RECORD -> {
                            viewModel.wearableAccuracy = dataMap.getDouble(KEY_RECORD)
                            viewModel.changeIsPlaying(PlayState.PAUSE)
                        }
                    }
                }
        }
        wearableDataClient.addListener(wearableDataListener)
        onDispose { wearableDataClient.removeListener(wearableDataListener) }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.changeIsPlaying(PlayState.DEFAULT)
            viewModel.pauseMusic(false)
        }
    }

    RhythmScreen(
        rhythmState = rhythmState,
        lottiePlaying = lottiePlaying,
        lottieLoading = lottieLoading,
        onToggleSelected = viewModel::changeSelectedMode,
        onWatchBtnClick = { viewModel.showSyncDialog(true) },
        onPlayBtnClick = { viewModel.changeIsPlaying(PlayState.PLAYING) },
        onPauseBtnClick = { viewModel.changeIsPlaying(PlayState.PAUSE) },
        onChangeBtnClick = { viewModel.showBottomSheet(true) }
    )

    if (rhythmState.isBottomSheetVisible) {
        RhythmBottomSheet(
            rhythmState = rhythmState,
            sheetState = sheetState,
            modifier = Modifier.heightIn(max = maxHeight),
            onDismissRequest = { viewModel.showBottomSheet(false) },
            onSubmitBtnClick = { bit, bpm ->
                scope.launch {
                    viewModel.updateRhythm(bit, bpm)
                    sheetState.hide()
                    viewModel.showBottomSheet(false)
                }
            }
        )
    }

    if (rhythmState.isSaveDialogVisible) {
        TwoButtonDialog(
            content = stringResource(R.string.rhythm_stop_tv_title),
            firstBtnText = stringResource(R.string.rhythm_stop_btn_save),
            secondBtnText = stringResource(R.string.rhythm_stop_btn_pause),
            onFirstBtnClick = { viewModel.changeIsPlaying(PlayState.STOP) },
            onSecondBtnClick = { viewModel.showSaveDialog(false) },
            onDismissRequest = { viewModel.showSaveDialog(false) },
        )
    }

    if (rhythmState.isSyncDialogVisible) {
        OneButtonDialog(
            content = stringResource(R.string.rhythm_wearable_sync),
            onConfirmClick = viewModel::sendBpmToWearable,
            onDismissRequest = { viewModel.showSyncDialog(false) },
        )
    }
}

@Composable
private fun RhythmScreen(
    rhythmState: RhythmState,
    lottiePlaying: LottieComposition?,
    lottieLoading: LottieComposition?,
    onToggleSelected: (RhythmMode) -> Unit = {},
    onWatchBtnClick: () -> Unit = {},
    onPlayBtnClick: () -> Unit = {},
    onPauseBtnClick: () -> Unit = {},
    onChangeBtnClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RhythmPlayBtnWithLottie(
            rhythmState = rhythmState,
            lottieComposition = lottiePlaying,
            onPlayBtnClick = onPlayBtnClick,
            onStopBtnClick = onPauseBtnClick
        )

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
                    WatchSyncBtn(onWatchBtnClick)
                }
            }

            RhythmTitleText(
                rhythmState = rhythmState
            )

            Spacer(modifier = Modifier.weight(1f))

            if (rhythmState.selectedMode == RhythmMode.RHYTHM) {
                RhythmInfoChips(
                    rhythmState = rhythmState,
                    onChangeBtnClick = onChangeBtnClick
                )
                RhythmChangeBtn(
                    onChangeBtnClick = onChangeBtnClick
                )
            } else {
                FixedText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.rhythm_stretch_info),
                    style = StempoTheme.typography.body2,
                    color = Gray500,
                )
            }
        }

        if (rhythmState.isLoading) {
            LottieAnimation(
                composition = lottieLoading,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Transparent50)
                    .padding(horizontal = 50.dp)
                    .clickableWithoutRipple { }
            )
        }
    }
}

@Composable
fun RhythmPlayBtnWithLottie(
    rhythmState: RhythmState,
    lottieComposition: LottieComposition?,
    onPlayBtnClick: () -> Unit = {},
    onStopBtnClick: () -> Unit = {}
) {
    Image(
        imageVector = ImageVector.vectorResource(id = rhythmState.imageResource),
        contentDescription = null,
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
    )
    Image(
        imageVector = ImageVector.vectorResource(
            id = if (rhythmState.isPlaying == PlayState.PLAYING) R.drawable.ic_stop else R.drawable.ic_play
        ),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clickableWithoutRipple { if (rhythmState.isPlaying == PlayState.PLAYING) onStopBtnClick() else onPlayBtnClick() }
    )
    if (rhythmState.isPlaying == PlayState.PLAYING) {
        LottieAnimation(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            speed = rhythmState.animationSpeed,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .scale(1.5f)
        )
    }
}

@Composable
fun WatchSyncBtn(
    onWatchBtnClick: () -> Unit = {}
) {
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.ic_watch),
        contentDescription = null,
        modifier = Modifier
            .padding(end = 8.dp)
            .clickableWithoutRipple { onWatchBtnClick() }
            .padding(6.dp)
    )
}

@Composable
fun ColumnScope.RhythmTitleText(
    rhythmState: RhythmState
) {
    FixedText(
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
}

@Composable
fun ColumnScope.RhythmInfoChips(
    rhythmState: RhythmState,
    onChangeBtnClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .align(Alignment.CenterHorizontally)
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

@Composable
fun RhythmChangeBtn(
    onChangeBtnClick: () -> Unit = {}
) {
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
        FixedText(
            text = stringResource(id = R.string.rhythm_btn_change_level),
            style = StempoTheme.typography.head4,
            color = White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmScreenPreview() {
    StempoTheme {
        RhythmScreen(
            rhythmState = RhythmState(),
            lottiePlaying = null,
            lottieLoading = null
        )
    }
}