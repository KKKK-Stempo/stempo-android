package com.kkkk.presentation.main.rhythm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.entity.request.RhythmRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.main.rhythm.RhythmState.Companion.STRETCH_MUSIC_FILE
import com.kkkk.presentation.main.rhythm.manager.MusicManager
import com.kkkk.presentation.main.rhythm.model.PlayState
import com.kkkk.presentation.main.rhythm.model.RhythmMode
import com.kkkk.presentation.manager.AmplitudeManager
import com.kkkk.presentation.manager.PhoneDataManager
import com.kkkk.stempo.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    private val rhythmRepository: RhythmRepository,
    private val userRepository: UserRepository,
    private val phoneDataManager: PhoneDataManager,
    private val musicManager: MusicManager
) : ViewModel() {
    private val _rhythmState = MutableStateFlow(RhythmState())
    val rhythmState = _rhythmState.asStateFlow()

    private val _rhythmSideEffect = MutableSharedFlow<RhythmSideEffect>()
    val rhythmSideEffect = _rhythmSideEffect.asSharedFlow()

    private val _oddStepCount = MutableStateFlow(0)
    private val _oddStepTime = MutableStateFlow(0L)

    private val _evenStepCount = MutableStateFlow(0)
    private val _evenStepTime = MutableStateFlow(0L)

    private val _beforeStepTime = MutableStateFlow(0L)

    var wearableAccuracy: Double = 0.0

    init {
        initRhythmFromDataStore()
    }

    private fun initRhythmFromDataStore() {
        _rhythmState.update {
            it.copy(bit = userRepository.getBit(), bpm = userRepository.getBpm())
        }
    }

    fun changeSelectedMode(selectedMode: RhythmMode) {
        _rhythmState.update { it.copy(selectedMode = selectedMode, isPlaying = PlayState.DEFAULT) }
        AmplitudeManager.trackEvent("change_rhythm_mode", mapOf("mode" to selectedMode.name))
    }

    fun changeIsPlaying(isPlaying: PlayState) {
        _rhythmState.update { it.copy(isPlaying = isPlaying) }
        AmplitudeManager.trackEvent("change_rhythm_play_state", mapOf("state" to isPlaying.name))
    }

    private fun changeIsLoading(isLoading: Boolean) {
        _rhythmState.update { it.copy(isLoading = isLoading) }
    }

    fun showBottomSheet(show: Boolean) {
        _rhythmState.update { it.copy(isBottomSheetVisible = show, isPlaying = PlayState.DEFAULT) }
        if (show) AmplitudeManager.trackEvent("show_rhythm_bottom_sheet")
    }

    fun showSaveDialog(show: Boolean) {
        _rhythmState.update { it.copy(isSaveDialogVisible = show) }
        if (show) AmplitudeManager.trackEvent("show_rhythm_save_dialog")
    }

    fun showSyncDialog(show: Boolean) {
        _rhythmState.update { it.copy(isSyncDialogVisible = show) }
        if (show) AmplitudeManager.trackEvent("show_rhythm_sync_dialog")
    }

    fun updateRhythm(bit: Int, bpm: Int) {
        _rhythmState.update { it.copy(bit = bit, bpm = bpm, isPlaying = PlayState.DEFAULT) }
        userRepository.setBpm(bpm)
        userRepository.setBit(bit)
        AmplitudeManager.apply {
            trackEvent("change_rhythm_bit_bpm", mapOf("bit" to bit, "bpm" to bpm))
            updateIntProperties("bit", bit)
            updateIntProperties("bpm", bpm)
        }
    }

    fun updateIsPlayerLoaded(isPlayerLoaded: Boolean) {
        _rhythmState.update { it.copy(isPlayerLoaded = isPlayerLoaded) }
    }

    fun loadMusicPlayers() {
        viewModelScope.launch {
            runCatching {
                val (resourceId, speed, filename) = if (rhythmState.value.selectedMode == RhythmMode.STRETCH) {
                    Triple(R.raw.music_stretch, 1.0f, STRETCH_MUSIC_FILE)
                } else {
                    Triple(
                        rhythmState.value.musicByBpm,
                        rhythmState.value.speedByBpm,
                        rhythmState.value.filename
                    )
                }
                musicManager.load(resourceId, speed, filename)
            }.onSuccess {
                updateIsPlayerLoaded(true)
                changeIsLoading(false)
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    fun releaseMusicPlayers() {
        musicManager.release()
    }

    fun playMusic() {
        if (rhythmState.value.isPlayerLoaded) {
            musicManager.play()
        } else {
            viewModelScope.launch {
                changeIsPlaying(PlayState.DEFAULT)
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    fun pauseMusic(isDialogNeeded: Boolean) {
        musicManager.pause()
        if (isDialogNeeded && rhythmState.value.selectedMode == RhythmMode.RHYTHM) {
            showSaveDialog(true)
        }
    }

    fun downloadNewMusicFile(filePath: Path) {
        changeIsLoading(true)
        viewModelScope.launch {
            runCatching {
                val url = getRhythmUrl()
                val wavFile = getRhythmFile(url)
                saveRhythmFile(wavFile, filePath)
            }.onSuccess {
                updateIsPlayerLoaded(false)
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private suspend fun getRhythmUrl(): String =
        rhythmRepository.postToGetRhythmUrl(
            RhythmRequestModel(
                rhythmState.value.bpm,
                rhythmState.value.bit
            )
        ).getOrThrow()

    private suspend fun getRhythmFile(url: String): ByteArray =
        rhythmRepository.getRhythmWav(url).getOrThrow()

    private suspend fun saveRhythmFile(wavFile: ByteArray, filePath: Path) {
        runCatching {
            Files.newOutputStream(filePath).use { outputStream ->
                outputStream.write(wavFile)
                outputStream.flush()
            }
        }.getOrThrow()
    }

    fun addStepCount() {
        _rhythmState.update { it.copy(stepCount = it.stepCount + 1) }
        val currentTime = System.currentTimeMillis()
        if (rhythmState.value.stepCount < 2) {
            _beforeStepTime.value = currentTime
            return
        }
        val isEven = rhythmState.value.stepCount % 2 == 0
        val elapsedTime = currentTime - _beforeStepTime.value
        if (isEven) {
            _evenStepCount.value++
            _evenStepTime.value += elapsedTime
        } else {
            _oddStepCount.value++
            _oddStepTime.value += elapsedTime
        }
        _beforeStepTime.value = currentTime
    }

    fun recordCurrentStepAccuracy() {
        val accuracy = calculateAccuracy()
        if (accuracy == 0.0) {
            resetStepCount()
        } else {
            postRhythmRecordToSave(accuracy)
        }
    }

    private fun postRhythmRecordToSave(accuracy: Double) {
        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(
                    accuracy = accuracy,
                    duration = _oddStepTime.value.toInt() + _evenStepTime.value.toInt(),
                    steps = rhythmState.value.stepCount,
                    leftFootAverageSpeed = _oddStepTime.value / _oddStepCount.value,
                    rightFootAverageSpeed = _evenStepTime.value / _evenStepCount.value,
                    bit = rhythmState.value.bit,
                    bpm = rhythmState.value.bpm
                )
            ).onSuccess {
                resetStepCount()
                _rhythmSideEffect.emit(RhythmSideEffect.SaveSuccessToast)
                AmplitudeManager.trackEvent("save_rhythm_record", mapOf("accuracy" to accuracy))
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private fun calculateAccuracy(): Double {
        when {
            wearableAccuracy != 0.0 -> {
                return wearableAccuracy
            }

            _oddStepCount.value == 0 || _evenStepCount.value == 0 -> {
                return 0.0
            }

            else -> {
                val time1 = _oddStepTime.value.toDouble() / _oddStepCount.value
                val time2 = _evenStepTime.value.toDouble() / _evenStepCount.value
                return (1.0 - kotlin.math.abs(time1 - time2) / (time1 + time2)) * 100
            }
        }
    }

    private fun resetStepCount() {
        showSaveDialog(false)
        updateIsPlayerLoaded(false)
        _rhythmState.update { it.copy(stepCount = 0) }
        _oddStepCount.value = 0
        _evenStepCount.value = 0
        _oddStepTime.value = 0
        _evenStepTime.value = 0
        wearableAccuracy = 0.0
    }

    fun sendBpmToWearable() {
        phoneDataManager.sendIntToWearable(
            PhoneDataManager.PATH_BPM,
            PhoneDataManager.KEY_BPM,
            rhythmState.value.bpm
        )
        showSyncDialog(false)
    }

    companion object {
        const val KEY_RECORD = "KEY_RECORD"
        const val KEY_START = "KEY_START"
        const val KEY_END = "KEY_END"

        const val PATH_RECORD = "/record"
        const val PATH_START = "/start"
        const val PATH_END = "/end"
    }
}