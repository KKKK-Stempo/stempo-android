package com.kkkk.presentation.main.rhythm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.kkkk.presentation.manager.PhoneDataManager
import com.kkkk.presentation.manager.PhoneDataManager.Companion.KEY_BPM
import com.kkkk.presentation.manager.PhoneDataManager.Companion.PATH_BPM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentRhythmBinding
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm),
    SensorEventListener, DataClient.OnDataChangedListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val viewModel by activityViewModels<RhythmViewModel>()
    private var rhythmBottomSheet: RhythmBottomSheet? = null
    private var rhythmSaveDialog: RhythmSaveDialog? = null

    private lateinit var soundPool: SoundPool
    private var beatSound: Int = 0
    private var musicSound: Int = 0

    @Inject
    lateinit var phoneDataManager: PhoneDataManager

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
        binding.btnChangeLevel.setOnSingleClickListener {
            rhythmBottomSheet = RhythmBottomSheet()
            rhythmBottomSheet?.show(parentFragmentManager, BOTTOM_SHEET_CHANGE_LEVEL)
        }
    }

    private fun initStretchNavigateBtnListener() {
        binding.btnStretchMode.setOnSingleClickListener {
            viewModel.navigateToStretchView(true)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnRhythmPlay.setOnSingleClickListener {
            if (::soundPool.isInitialized && viewModel.isSoundLoaded) {
                if (!viewModel.isPlayed) {
                    with(soundPool) {
                        play(musicSound, 1f, 1f, 1, -1, 1f)
                        play(beatSound, 1f, 1f, 1, -1, 1f)
                    }
                    viewModel.isPlayed = true
                } else {
                    soundPool.autoResume()
                }
                switchPlayingState(true)
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }
    }

    private fun initStopBtnListener() {
        binding.btnRhythmStop.setOnSingleClickListener {
            if (::soundPool.isInitialized && viewModel.isSoundLoaded) {
                soundPool.autoPause()
                switchPlayingState(false)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            rhythmSaveDialog = RhythmSaveDialog()
            rhythmSaveDialog?.show(parentFragmentManager, DIALOG_RHYTHM_SAVE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::soundPool.isInitialized && viewModel.isSoundLoaded) {
            soundPool.autoPause()
            switchPlayingState(false)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun switchPlayingState(start: Boolean) {
        with(binding) {
            btnRhythmPlay.isVisible = !start
            btnRhythmStop.isVisible = start
            lottieRhythmBg.isVisible = start
        }
    }

    private fun initWearableSyncBtnListener() {
        binding.tvRhythmTitle.setOnSingleClickListener {
            phoneDataManager.sendIntToWearable(PATH_BPM, KEY_BPM, viewModel.bpm)
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
                if (::soundPool.isInitialized && viewModel.isSoundLoaded) {
                    soundPool.autoPause()
                    switchPlayingState(false)
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
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
                            setLoadingView(false)
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
        }.onSuccess {
            setMediaPlayer()
        }.onFailure {
            toast(stringOf(R.string.error_msg))
        }
    }

    private fun setMediaPlayer() {
        if (::soundPool.isInitialized) soundPool.release()
        soundPool = SoundPool.Builder().setMaxStreams(2).build()
        beatSound =
            soundPool.load(File(requireContext().filesDir, viewModel.filename).absolutePath, 1)
        musicSound = soundPool.load(requireContext(), R.raw.music_bpm_100, 1)
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && (sampleId == musicSound || sampleId == beatSound)) {
                viewModel.isSoundLoaded = true
                setLoadingView(false)
            }
        }
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
        rhythmBottomSheet = null
        rhythmSaveDialog = null
    }

    override fun onResume() {
        super.onResume()
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        Timber.tag("okhttp").d("LISTENER : ADDED")
        Wearable.getDataClient(requireActivity()).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
        Timber.tag("okhttp").d("LISTENER : REMOVED")
        Wearable.getDataClient(requireActivity()).removeListener(this)
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Timber.tag("okhttp").d("LISTENER : ON DATA CHANGED")

        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo(PATH_RECORD) == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            val record = getDouble(KEY_RECORD)
                            Timber.tag("okhttp").d("LISTENER : DATA RECEIVED : $record")
                            viewModel.posRhythmRecordToSaveWatch(record)
                        }
                    }
                }
            }
        }
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

        const val KEY_RECORD = "KEY_RECORD"
        const val PATH_RECORD = "/record"

        private const val FLOAT_120 = 120.00000000000000000000F

        private const val SUCCESS_CODE = 200
    }
}