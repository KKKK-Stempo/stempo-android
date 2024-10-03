package com.kkkk.presentation.main.rhythm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.drawableOf
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.core.state.UiState
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.FragmentRhythmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import kotlin.coroutines.resume

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm),
    SensorEventListener, DataClient.OnDataChangedListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val viewModel by activityViewModels<RhythmViewModel>()

    private var rhythmBottomSheet: RhythmBottomSheet? = null
    private var rhythmSaveDialog: RhythmSaveDialog? = null
    private var watchSyncDialog: WatchSyncDialog? = null

    private lateinit var soundPool: SoundPool
    private lateinit var mediaPlayer: MediaPlayer

    private var beatSound: Int = 0
    private var beatStream: Int = 0
    private var isLoaded = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setLoadingView(true)
        initChangeRhythmBtnListener()
        initStretchNavigateBtnListener()
        initPlayBtnListener()
        initStopBtnListener()
        initWearableSyncBtnListener()
        initExistingRhythm()
        initializeSensor()
        observeStepCount()
        observeRhythmChanged()
        observeRhythmUrlState()
        observeDownloadState()
        observeRecordSaveState()
    }

    private fun initChangeRhythmBtnListener() {
       with(binding) {
           btnChangeLevel.setOnSingleClickListener { startRhythmBottomSheet()}
           tvRhythmBpm.setOnSingleClickListener { startRhythmBottomSheet()}
           tvRhythmBit.setOnSingleClickListener { startRhythmBottomSheet()}
       }
    }

    private fun startRhythmBottomSheet() {
        rhythmBottomSheet = RhythmBottomSheet()
        rhythmBottomSheet?.show(parentFragmentManager, BOTTOM_SHEET_CHANGE_LEVEL)
    }

    private fun initStretchNavigateBtnListener() {
        binding.btnStretchMode.setOnSingleClickListener {
            viewModel.navigateToStretchView(true)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnRhythmPlay.setOnSingleClickListener {
            if (::soundPool.isInitialized && ::mediaPlayer.isInitialized && isLoaded) {
                lifecycleScope.launch {
                    playSoundPoolAndMediaPlayer()
                    viewModel.resetStepInfo()
                }
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }
    }

    private suspend fun playSoundPoolAndMediaPlayer() {
        lifecycleScope.launch {
            listOf(
                async { playMediaPlayerWithSpeed() },
                async { playOrResumeSoundPool() },
            ).awaitAll()
            switchPlayingState(true)
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun playMediaPlayerWithSpeed() {
        mediaPlayer.apply {
            setPlaybackParams(PlaybackParams().setSpeed(findSpeedByBpm(viewModel.bpm)))
        }.start()
    }

    private fun playOrResumeSoundPool() {
        if (beatStream != 0) {
            soundPool.resume(beatStream)
        } else {
            beatStream = soundPool.play(beatSound, 10f, 10f, 1, -1, 1f)
        }
    }

    private fun initStopBtnListener() {
        binding.btnRhythmStop.setOnSingleClickListener {
            pauseMusic(true)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::mediaPlayer.isInitialized && ::soundPool.isInitialized) pauseMusic(false)
    }

    private fun pauseMusic(isButton: Boolean) {
        lifecycleScope.launch {
            listOf(
                async { if (beatStream != 0) soundPool.pause(beatStream) },
                async { mediaPlayer.pause() }
            ).awaitAll()
            switchPlayingState(false)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (isButton) {
                rhythmSaveDialog = RhythmSaveDialog()
                rhythmSaveDialog?.show(parentFragmentManager, DIALOG_RHYTHM_SAVE)
            }
        }
    }

    private fun switchPlayingState(start: Boolean) {
        with(binding) {
            btnRhythmPlay.isVisible = !start
            btnRhythmStop.isVisible = start
            lottieRhythmBg.isVisible = start
        }
    }

    private fun initExistingRhythm() {
        setUiWithCurrentRhythm()
        viewModel.postToGetRhythmUrlFromServer()
    }

    private fun observeStepCount() {
        viewModel.stepCount.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach {
            binding.tvRhythmStep.text =
                getString(R.string.rhythm_tv_step, viewModel.stepCount.value)
        }.launchIn(lifecycleScope)
    }

    private fun observeRhythmChanged() {
        viewModel.isRhythmChanged.flowWithLifecycle(lifecycle).onEach { isChanged ->
            if (isChanged) {
                setLoadingView(true)
                pauseMusic(false)
                setUiWithCurrentRhythm()
                viewModel.resetRhythmChangedState()
                viewModel.postToGetRhythmUrlFromServer()
            }
        }.launchIn(lifecycleScope)
    }

    private fun setUiWithCurrentRhythm() {
        val color = when (viewModel.bit) {
            2 -> COLOR_PURPLE
            3 -> COLOR_SKY
            4 -> COLOR_GREEN
            6 -> COLOR_PURPLE
            8 -> COLOR_SKY
            else -> return
        }
        with(binding) {
            tvRhythmBpm.apply {
                text = getString(R.string.rhythm_tv_bpm, viewModel.bpm)
                setTextColor(colorOf(getResource("${color}_50", COLOR)))
                background =
                    drawableOf(getResource("shape_white_fill_${color}50_line_17_rect", DRAWABLE))
            }
            tvRhythmBit.apply {
                text = getString(R.string.rhythm_tv_bit, viewModel.bit)
                background =
                    drawableOf(getResource("shape_${color}50_fill_17_rect", DRAWABLE))
            }
            ivRhythmBg.setImageResource(getResource("img_rhythm_bg_$color", DRAWABLE))
            lottieRhythmBg.apply {
                setAnimation(getResource("stempo_rhythm_$color", RAW))
                speed = viewModel.bpm / FLOAT_80
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
                            setMusicPlayer()
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
        }.onSuccess {
            setMusicPlayer()
        }.onFailure {
            toast(stringOf(R.string.error_msg))
        }
    }

    private fun setMusicPlayer() {
        lifecycleScope.launch {
            beatStream = 0
            listOf(
                async { setSoundPoolAsync() },
                async { setMediaPlayerAsync() }
            ).awaitAll()
            isLoaded = true
            setLoadingView(false)
        }
    }

    private suspend fun setSoundPoolAsync() = suspendCancellableCoroutine { continuation ->
        if (File(requireContext().filesDir, viewModel.filename).exists()) {
            if (::soundPool.isInitialized) soundPool.release()
            soundPool = SoundPool.Builder().setMaxStreams(1).build().apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0 && sampleId == beatSound) {
                        continuation.resume(Unit)
                    }
                }
            }
            beatSound =
                soundPool.load(File(requireContext().filesDir, viewModel.filename).absolutePath, 1)
        } else {
            toast(stringOf(R.string.error_msg))
            continuation.resume(Unit)
        }
        continuation.invokeOnCancellation {
            if (::soundPool.isInitialized) soundPool.release()
        }
    }

    private suspend fun setMediaPlayerAsync() = suspendCancellableCoroutine { continuation ->
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(requireContext(), findMusicByBpm(viewModel.bpm)).apply {
            isLooping = true
            setVolume(0.2f, 0.2f)
            setOnPreparedListener {
                continuation.resume(Unit)
            }
        }
        continuation.invokeOnCancellation {
            if (::mediaPlayer.isInitialized) mediaPlayer.release()
        }
    }

    private fun setLoadingView(isLoading: Boolean) {
        binding.layoutLoading.isVisible = isLoading

        if (isLoading) {
            setStatusBarColor(R.color.transparent_50)
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            setStatusBarColor(R.color.white)
            requireActivity().window.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            viewModel.addStepCount(1)
        }
    }

    private fun initializeSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    private fun observeRecordSaveState() {
        viewModel.isRecordSaved.flowWithLifecycle(lifecycle).onEach { isSuccess ->
            if (isSuccess) {
                toast(stringOf(R.string.rhythm_toast_save_success))
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        if (::soundPool.isInitialized) soundPool.release()
        rhythmBottomSheet = null
        rhythmSaveDialog = null
        watchSyncDialog = null
    }

    private fun initWearableSyncBtnListener() {
        binding.btnWatch.setOnSingleClickListener {
            watchSyncDialog = WatchSyncDialog()
            watchSyncDialog?.show(parentFragmentManager, DIALOG_WATCH_SYNC)
        }
    }

    override fun onResume() {
        super.onResume()
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        Wearable.getDataClient(requireActivity()).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
        Wearable.getDataClient(requireActivity()).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.tag("okhttp").d("LISTENER : ON DATA CHANGED")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    when (item.uri.path) {
                        PATH_START -> handleStart(dataMap)
                        PATH_RECORD -> handleRecord(dataMap)
                        PATH_END -> handleEnd(dataMap)
                        else -> Timber.tag("okhttp").d("LISTENER : Unknown path received")
                    }
                }
            }
        }
    }

    private fun handleStart(dataMap: DataMap) {
        Timber.tag("okhttp").d("LISTENER : START DATA RECEIVED : ${dataMap.getDouble(KEY_START)}")
        if (::soundPool.isInitialized && ::mediaPlayer.isInitialized && isLoaded) {
            lifecycleScope.launch {
                playSoundPoolAndMediaPlayer()
            }
        } else {
            toast(stringOf(R.string.error_msg))
        }
    }

    private fun handleRecord(dataMap: DataMap) {
        val record = dataMap.getDouble(KEY_RECORD)
        Timber.tag("okhttp").d("LISTENER : RECORD DATA RECEIVED : $record")
        viewModel.watchAccuracy = record
        pauseMusic(true)
    }

    private fun handleEnd(dataMap: DataMap) {
        Timber.tag("okhttp").d("LISTENER : END DATA RECEIVED : ${dataMap.getDouble(KEY_END)}")
        pauseMusic(false)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val BOTTOM_SHEET_CHANGE_LEVEL = "BOTTOM_SHEET_CHANGE_LEVEL"
        private const val DIALOG_RHYTHM_SAVE = "DIALOG_RHYTHM_SAVE"
        private const val DIALOG_WATCH_SYNC = "DIALOG_WATCH_SYNC"

        private const val COLOR_PURPLE = "purple"
        private const val COLOR_SKY = "sky"
        private const val COLOR_GREEN = "green"

        private const val COLOR = "color"
        private const val DRAWABLE = "drawable"
        private const val RAW = "raw"

        const val KEY_RECORD = "KEY_RECORD"
        const val KEY_START = "KEY_START"
        const val KEY_END = "KEY_END"

        const val PATH_RECORD = "/record"
        const val PATH_START = "/start"
        const val PATH_END = "/end"

        private const val FLOAT_80 = 80.00000000000000000000F

        private const val SUCCESS_CODE = 200

        fun findMusicByBpm(bpm: Int) = when (bpm / 20) {
            3 -> R.raw.music_bpm_60
            4 -> R.raw.music_bpm_80
            5 -> R.raw.music_bpm_100
            6 -> R.raw.music_bpm_120
            else -> R.raw.music_bpm_60
        }

        fun findSpeedByBpm(bpm: Int) = when (bpm % 20) {
            0 -> 1.0f
            5 -> 1.08f
            10 -> 1.16f
            15 -> 1.25f
            else -> 1.0f
        }
    }
}