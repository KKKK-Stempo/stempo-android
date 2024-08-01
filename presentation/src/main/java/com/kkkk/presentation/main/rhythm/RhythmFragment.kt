package com.kkkk.presentation.main.rhythm

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.drawableOf
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.core.state.UiState
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.LEVEL_UNDEFINED
import com.kkkk.presentation.onboarding.onbarding.OnboardingViewModel.Companion.SPEED_CALC_INTERVAL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentRhythmBinding
import java.io.File
import java.nio.file.Files

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm),
    SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val viewModel by activityViewModels<RhythmViewModel>()
    private var rhythmBottomSheet: RhythmBottomSheet? = null
    private var rhythmSaveDialog: RhythmSaveDialog? = null
    private lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initChangeLevelBtnListener()
        initPlayBtnListener()
        initStopBtnListener()
        observeRhythmLevel()
        observeRhythmUrlState()
        observeDownloadState()
        observeRecordSaveState()
    }

    private fun initChangeLevelBtnListener() {
        binding.btnChangeLevel.setOnSingleClickListener {
            rhythmBottomSheet = RhythmBottomSheet()
            rhythmBottomSheet?.show(parentFragmentManager, BOTTOM_SHEET_CHANGE_LEVEL)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnRhythmPlay.setOnSingleClickListener {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.start()
                switchPlayingState(true)
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }
    }

    private fun initStopBtnListener() {
        binding.btnRhythmStop.setOnSingleClickListener {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.pause()
                switchPlayingState(false)
            }
            rhythmSaveDialog = RhythmSaveDialog()
            rhythmSaveDialog?.show(parentFragmentManager, DIALOG_RHYTHM_SAVE)
        }
    }

    private fun switchPlayingState(start: Boolean) {
        with(binding) {
            btnRhythmPlay.isVisible = !start
            btnRhythmStop.isVisible = start
            lottieRhythmBg.isVisible = start
        }
    }

    private fun observeRhythmLevel() {
        viewModel.rhythmLevel.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { level ->
            if (level == LEVEL_UNDEFINED) return@onEach
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.pause()
                switchPlayingState(false)
            }
            setUiWithCurrentLevel()
            viewModel.postToGetRhythmUrlFromServer(level)
        }.launchIn(lifecycleScope)
    }

    private fun setUiWithCurrentLevel() {
        val color = when (viewModel.rhythmLevel.value.rem(3)) {
            1 -> COLOR_PURPLE
            2 -> COLOR_SKY
            0 -> COLOR_GREEN
            else -> return
        }
        with(binding) {
            tvRhythmLevel.apply {
                text = getString(R.string.rhythm_tv_level, viewModel.rhythmLevel.value)
                setTextColor(colorOf(getResource("${color}_50", COLOR)))
                background =
                    drawableOf(getResource("shape_white_fill_${color}50_line_17_rect", DRAWABLE))
            }
            tvRhythmStep.apply {
                setTextColor(colorOf(getResource("${color}_50", COLOR)))
                background =
                    drawableOf(getResource("shape_white_fill_${color}50_line_17_rect", DRAWABLE))
            }
            ivRhythmBg.setImageResource(getResource("img_rhythm_bg_$color", DRAWABLE))
            lottieRhythmBg.apply {
                setAnimation(getResource("stempo_rhythm_$color", RAW))
                speed = viewModel.bpm / FLOAT_120
                playAnimation()
            }
        }
    }

    private fun getResource(name: String, defType: String) =
        resources.getIdentifier(name, defType, requireContext().packageName)

    private fun observeRhythmUrlState() {
        viewModel.rhythmUrlState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (File(requireContext().filesDir, viewModel.filename).exists()) {
                            setMediaPlayer()
                        } else {
                            setLoadingView(true)
                            viewModel.getRhythmWavFile(state.data)
                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeDownloadState() {
        viewModel.downloadWavState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> saveWavFile(state.data)

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun saveWavFile(byteArray: ByteArray) {
        runCatching {
            Files.newOutputStream(
                File(
                    requireContext().filesDir,
                    viewModel.filename
                ).toPath()
            ).use { outputStream ->
                outputStream.write(byteArray)
                outputStream.flush()
            }
        }
            .onSuccess {
                setMediaPlayer()
            }
            .onFailure {
                toast(stringOf(R.string.error_msg))
            }
    }

    private fun setMediaPlayer() {
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                File(
                    requireContext().filesDir,
                    viewModel.filename
                ).absolutePath
            )
            prepare()
        }
        setLoadingView(false)
    }

    private fun setLoadingView(isLoading: Boolean) {
        binding.layoutLoading.isVisible = isLoading
        if (isLoading) {
            setStatusBarColor(R.color.transparent_50)
        } else {
            setStatusBarColor(R.color.white)
        }
    }

    private fun observeRecordSaveState() {
        viewModel.recordSaveState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        toast(stringOf(R.string.rhythm_toast_save_success))
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rhythmBottomSheet = null
        rhythmSaveDialog = null
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SUCCESS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensor()
            } else {
                // 사용자가 권한을 거부한 경우 처리
                // 예: 사용자에게 권한의 필요성을 설명하는 다이얼로그 표시
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            viewModel.addStepCount(1)

            if (viewModel.stepCount.value % SPEED_CALC_INTERVAL == 0) {
                calculateSpeed()
            }
        }
    }

    private fun initializeSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    private fun calculateSpeed() {
        val currentTime = System.currentTimeMillis()
        val lastStepTime = viewModel.lastStepTime.value
        if (lastStepTime != 0L) {
            val timeDiff = currentTime - lastStepTime
            val speed = (SPEED_CALC_INTERVAL / (timeDiff / 1000.0)) * 60 // 분당 걸음 수

            viewModel.setSpeed(speed)
        }
        viewModel.setLastStepTime(currentTime)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val BOTTOM_SHEET_CHANGE_LEVEL = "BOTTOM_SHEET_CHANGE_LEVEL"
        private const val DIALOG_RHYTHM_SAVE = "DIALOG_RHYTHM_SAVE"

        private const val COLOR_PURPLE = "purple"
        private const val COLOR_SKY = "sky"
        private const val COLOR_GREEN = "green"

        private const val COLOR = "color"
        private const val DRAWABLE = "drawable"
        private const val RAW = "raw"

        private const val FLOAT_120 = 120.00000000000000000000F

        private const val SUCCESS_CODE = 200
    }
}