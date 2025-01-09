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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.kkkk.presentation.main.rhythm.RhythmState.Companion.STRETCH_MUSIC_FILE
import com.kkkk.presentation.main.rhythm.RhythmState.Companion.findMusicByBpm
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.FLOAT_80
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.KEY_RECORD
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_END
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_RECORD
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.PATH_START
import com.kkkk.presentation.main.rhythm.component.RhythmBottomSheet
import com.kkkk.presentation.main.rhythm.component.RhythmChip
import com.kkkk.presentation.main.rhythm.component.RhythmModeToggle
import com.kkkk.presentation.main.rhythm.component.RhythmStopDialog
import com.kkkk.presentation.main.rhythm.component.RhythmSyncDialog
import com.kkkk.presentation.main.rhythm.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent50
import com.kkkk.presentation.main.theme.White
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
    val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    val wearableDataClient = Wearable.getDataClient(context)

    // Lottie 및 애니메이션 속도 관리
    val lottiePlaying by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rhythmState.lottieResource)
    )
    val lottieLoading by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.stempo_loading)
    )
    val animationSpeed = remember(rhythmState.selectedMode, rhythmState.bpm) {
        if (rhythmState.selectedMode == RhythmMode.RHYTHM) rhythmState.bpm / FLOAT_80 else 0.75F
    }

    // 바텀시트 관련 상태
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp * 0.9f

    val systemUiController = rememberSystemUiController()

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
            viewModel.getRhythmUrlState(File(context.filesDir, rhythmState.filename).toPath())
        } else {
            viewModel.updateIsPlayerLoaded(false)
        }
    }

    LaunchedEffect(rhythmState.isPlayerLoaded) {
        if (!rhythmState.isPlayerLoaded) {
            if (rhythmState.selectedMode == RhythmMode.RHYTHM) {
                viewModel.setMusicPlayer(
                    soundPoolFile = File(context.filesDir, rhythmState.filename),
                    mediaPlayerAfd = context.resources.openRawResourceFd(findMusicByBpm(rhythmState.bpm))
                )
            } else {
                viewModel.setMusicPlayer(
                    soundPoolFile = File(context.filesDir, STRETCH_MUSIC_FILE),
                    mediaPlayerAfd = context.resources.openRawResourceFd(R.raw.music_stretch)
                )
            }
        }
    }

    LaunchedEffect(rhythmState.isPlaying) {
        when (rhythmState.isPlaying) {
            PlayState.PLAYING -> viewModel.playMusic()
            PlayState.PAUSE -> viewModel.pauseMusic(true)
            PlayState.STOP -> viewModel.postRhythmRecordToSave()
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
        onDispose { viewModel.releaseMusicPlayers() }
    }

    RhythmScreen(
        rhythmState = rhythmState,
        lottiePlaying = lottiePlaying,
        lottieLoading = lottieLoading,
        animationSpeed = animationSpeed,
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
        RhythmStopDialog(
            onSaveClick = { viewModel.changeIsPlaying(PlayState.STOP) },
            onPauseClick = { viewModel.showSaveDialog(false) },
            onDismissRequest = { viewModel.showSaveDialog(false) },
        )
    }

    if (rhythmState.isSyncDialogVisible) {
        RhythmSyncDialog(
            onConfirmClick = viewModel::sendBpmToWearable,
            onDismissRequest = { viewModel.showSyncDialog(false) },
        )
    }
}

@Composable
internal fun RhythmScreen(
    rhythmState: RhythmState,
    lottiePlaying: LottieComposition?,
    lottieLoading: LottieComposition?,
    animationSpeed: Float = 1f,
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
            animationSpeed = animationSpeed,
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
            }

            RhythmChangeBtn(
                onChangeBtnClick = onChangeBtnClick
            )
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
    animationSpeed: Float = 1f,
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
            .padding(bottom = 10.dp)
    )
    Image(
        imageVector = ImageVector.vectorResource(
            id = if (rhythmState.isPlaying == PlayState.PLAYING) R.drawable.ic_stop else R.drawable.ic_play
        ),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .padding(bottom = 10.dp)
            .clickableWithoutRipple { if (rhythmState.isPlaying == PlayState.PLAYING) onStopBtnClick() else onPlayBtnClick() }
    )
    if (rhythmState.isPlaying == PlayState.PLAYING) {
        LottieAnimation(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            speed = animationSpeed,
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
}

@Composable
fun ColumnScope.RhythmInfoChips(
    rhythmState: RhythmState,
    onChangeBtnClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(bottom = 34.dp)
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
        Text(
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