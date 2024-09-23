package com.kkkk.stempo.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.kkkk.stempo.R
import com.kkkk.stempo.presentation.LocalWearableDataManager
import com.kkkk.stempo.presentation.home.HomeViewModel.Companion.VIBRATION_DURATION
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.KEY_RECORD
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.KEY_START
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.PATH_RECORD
import com.kkkk.stempo.presentation.manager.WearableDataManager.Companion.PATH_START
import kotlin.random.Random

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val wearableDataManager = LocalWearableDataManager.current
    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)

    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock =
        powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WearOS:KeepScreenOnWakeLock")

    sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                if (!state.isPlayingMusic) return

                viewModel.addStep()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // 정확도 변경 처리 (필요한 경우)
        }
    }

    LaunchedEffect(key1 = Unit) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        sensorManager.registerListener(
            sensorEventListener,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    LaunchedEffect(Unit) { // 화면 꺼짐 방지
        wakeLock.acquire()
    }

    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    200
                )
            }
        }
    }

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

                is HomeSideEffect.EndCount -> {
                    wearableDataManager.sendDoubleToPhone(
                        PATH_RECORD,
                        KEY_RECORD,
                        sideEffect.accuracy
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        MusicButton(isPlayingMusic = state.isPlayingMusic) {
            // TODO: 여기에서 전송 완료가 확인되면 진동을 시작하고 싶음. 일단 진동을 뺼까?
            viewModel.controlMusic()
            wearableDataManager.sendDoubleToPhone(
                PATH_START,
                KEY_START,
                Random.nextDouble(1.0, 10000.0)
            )
        }

        if (state.isPlayingMusic) {
            Text(
                text = state.stepCount.toString(),
                modifier = Modifier.align(Alignment.TopCenter)
            )
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
