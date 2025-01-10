package com.kkkk.presentation.main.rhythm

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.SoundPool
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.entity.request.RhythmRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.manager.PhoneDataManager
import com.kkkk.presentation.xmlmain.xmlrhythm.XmlRhythmFragment.Companion.findSpeedByBpm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class RhythmViewModel
@Inject
constructor(
    private val rhythmRepository: RhythmRepository,
    private val userRepository: UserRepository,
    private val phoneDataManager: PhoneDataManager
) : ViewModel() {
    private val _rhythmState = MutableStateFlow(RhythmState())
    val rhythmState = _rhythmState.asStateFlow()

    private val _rhythmSideEffect = MutableSharedFlow<RhythmSideEffect>()
    val rhythmSideEffect = _rhythmSideEffect.asSharedFlow()

    private var soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var mediaPlayer = MediaPlayer()

    private var beatSound: Int = 0
    private var beatStream: Int = 0

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
    }

    fun changeIsPlaying(isPlaying: PlayState) {
        _rhythmState.update { it.copy(isPlaying = isPlaying) }
    }

    private fun changeIsLoading(isLoading: Boolean) {
        _rhythmState.update { it.copy(isLoading = isLoading) }
    }

    fun showBottomSheet(show: Boolean) {
        _rhythmState.update { it.copy(isBottomSheetVisible = show, isPlaying = PlayState.DEFAULT) }
    }

    fun showSaveDialog(show: Boolean) {
        _rhythmState.update { it.copy(isSaveDialogVisible = show) }
    }

    fun showSyncDialog(show: Boolean) {
        _rhythmState.update { it.copy(isSyncDialogVisible = show) }
    }

    fun updateRhythm(bit: Int, bpm: Int) {
        _rhythmState.update { it.copy(bit = bit, bpm = bpm, isPlaying = PlayState.DEFAULT) }
        userRepository.setBpm(bpm)
        userRepository.setBit(bit)
    }

    fun updateIsPlayerLoaded(isPlayerLoaded: Boolean) {
        _rhythmState.update { it.copy(isPlayerLoaded = isPlayerLoaded) }
    }

    fun setMusicPlayer(soundPoolFile: File, mediaPlayerAfd: AssetFileDescriptor) {
        viewModelScope.launch {
            beatStream = 0
            listOf(
                async { setSoundPoolAsync(soundPoolFile) },
                async { setMediaPlayerAsync(mediaPlayerAfd) }
            ).awaitAll()
            updateIsPlayerLoaded(true)
            changeIsLoading(false)
        }
    }

    private suspend fun setSoundPoolAsync(file: File) {
        suspendCancellableCoroutine<Unit> { continuation ->
            if (file.exists()) {
                soundPool = SoundPool.Builder().setMaxStreams(1).build().apply {
                    setOnLoadCompleteListener { _, sampleId, _ ->
                        if (sampleId == beatSound) {
                            continuation.resume(Unit)
                        }
                    }
                }
                beatSound = soundPool.load(file.absolutePath, 1)
            } else {
                viewModelScope.launch {
                    _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
                    continuation.resume(Unit)
                }
            }
            continuation.invokeOnCancellation { soundPool.release() }
        }
    }

    private suspend fun setMediaPlayerAsync(afd: AssetFileDescriptor) {
        suspendCancellableCoroutine<Unit> { continuation ->
            mediaPlayer = MediaPlayer().apply {
                if (!isPlaying) {
                    reset()
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    isLooping = true
                    setVolume(0.2f, 0.2f)
                    prepareAsync()
                    continuation.resume(Unit)
                }
            }
        }
    }

    fun releaseMusicPlayers() {
        soundPool.release()
        mediaPlayer.release()
    }

    fun playMusic() {
        viewModelScope.launch {
            if (rhythmState.value.isPlayerLoaded) {
                listOf(
                    async { playMediaPlayerWithSpeed() },
                    async { playOrResumeSoundPool() },
                ).awaitAll()
            } else {
                changeIsPlaying(PlayState.DEFAULT)
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private fun playMediaPlayerWithSpeed() {
        mediaPlayer.apply {
            if (rhythmState.value.selectedMode == RhythmMode.RHYTHM) {
                playbackParams = PlaybackParams().setSpeed(findSpeedByBpm(rhythmState.value.bpm))
            }
        }.start()
    }

    private fun playOrResumeSoundPool() {
        if (beatStream != 0) {
            soundPool.resume(beatStream)
        } else {
            beatStream = soundPool.play(beatSound, 10f, 10f, 1, -1, 1f)
        }
    }

    fun pauseMusic(isDialogNeeded: Boolean) {
        viewModelScope.launch {
            listOf(
                async { if (beatStream != 0) soundPool.pause(beatStream) },
                async { runCatching { mediaPlayer.pause() } }
            ).awaitAll()
            if (isDialogNeeded && rhythmState.value.selectedMode == RhythmMode.RHYTHM) {
                showSaveDialog(true)
            }
        }
    }

    fun getRhythmUrlState(filePath: Path) {
        changeIsLoading(true)
        viewModelScope.launch {
            rhythmRepository.postToGetRhythmUrl(
                RhythmRequestModel(
                    rhythmState.value.bpm,
                    rhythmState.value.bit
                )
            ).onSuccess {
                getRhythmWavFile(it, filePath)
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private fun getRhythmWavFile(url: String, filePath: Path) {
        viewModelScope.launch {
            rhythmRepository.getRhythmWav(url)
                .onSuccess { wav ->
                    saveWavFile(wav, filePath)
                }.onFailure {
                    _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
                }
        }
    }

    private fun saveWavFile(wavFile: ByteArray, filePath: Path) {
        viewModelScope.launch {
            runCatching {
                Files.newOutputStream(filePath).use { outputStream ->
                    outputStream.write(wavFile)
                    outputStream.flush()
                }
            }.onSuccess {
                updateIsPlayerLoaded(false)
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    fun addStepCount() {
        _rhythmState.update { it.copy(stepCount = it.stepCount + 1) }
        if (rhythmState.value.stepCount < 2) {
            _beforeStepTime.value = System.currentTimeMillis()
            return
        }
        if (rhythmState.value.stepCount % 2 == 0) {
            _oddStepCount.value += 1
            _oddStepTime.value += System.currentTimeMillis() - _beforeStepTime.value
        } else {
            _evenStepCount.value += 1
            _evenStepTime.value += System.currentTimeMillis() - _beforeStepTime.value
        }
        _beforeStepTime.value = System.currentTimeMillis()
    }

    fun postRhythmRecordToSave() {
        val isInvalidStep =
            (_oddStepCount.value == 0 || _evenStepCount.value == 0) && wearableAccuracy == 0.0
        val accuracy = calculateAccuracy()
        if (isInvalidStep || accuracy == 0.0) {
            resetStepCount()
            return
        }
        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(
                    accuracy,
                    0,
                    rhythmState.value.stepCount
                )
            ).onSuccess {
                resetStepCount()
                _rhythmSideEffect.emit(RhythmSideEffect.SaveSuccessToast)
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private fun calculateAccuracy(): Double {
        return if (wearableAccuracy == 0.0) {
            val time1 = _oddStepTime.value.toDouble() / _oddStepCount.value
            val time2 = _evenStepTime.value.toDouble() / _evenStepCount.value
            (1.0 - kotlin.math.abs(time1 - time2) / (time1 + time2)) * 100
        } else {
            wearableAccuracy
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

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
        mediaPlayer.release()
    }

    companion object {
        const val FLOAT_80 = 80.00000000000000000000F

        const val KEY_RECORD = "KEY_RECORD"
        const val KEY_START = "KEY_START"
        const val KEY_END = "KEY_END"

        const val PATH_RECORD = "/record"
        const val PATH_START = "/start"
        const val PATH_END = "/end"
    }
}