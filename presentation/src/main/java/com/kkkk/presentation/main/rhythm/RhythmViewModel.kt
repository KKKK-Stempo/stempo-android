package com.kkkk.presentation.main.rhythm

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.SoundPool
import android.view.Choreographer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.entity.request.RhythmRequestModel
import com.kkkk.domain.repository.RhythmRepository
import com.kkkk.domain.repository.UserRepository
import com.kkkk.presentation.manager.PhoneDataManager
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
import kotlin.coroutines.resumeWithException

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
            runCatching {
                listOf(
                    async { setSoundPoolAsync(soundPoolFile) },
                    async { setMediaPlayerAsync(mediaPlayerAfd) }
                ).awaitAll()
            }.onSuccess {
                updateIsPlayerLoaded(true)
                changeIsLoading(false)
                mediaPlayerAfd.close()
            }.onFailure {
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private suspend fun setSoundPoolAsync(file: File) {
        suspendCancellableCoroutine<Unit> { continuation ->
            runCatching {
                soundPool = SoundPool.Builder().setMaxStreams(1).build().apply {
                    setOnLoadCompleteListener { _, sampleId, _ ->
                        if (sampleId == beatSound) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWithException(IllegalStateException())
                        }
                    }
                }
                beatStream = 0
                beatSound = soundPool.load(file.absolutePath, 1)
            }.onFailure { continuation.resumeWithException(it) }
        }
    }

    private suspend fun setMediaPlayerAsync(afd: AssetFileDescriptor) {
        suspendCancellableCoroutine<Unit> { continuation ->
            runCatching {
                mediaPlayer = MediaPlayer().apply {
                    reset()
                    setOnPreparedListener { continuation.resume(Unit) }
                    setOnErrorListener { _, _, _ ->
                        continuation.resumeWithException(IllegalStateException())
                        true
                    }
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    isLooping = true
                    setVolume(0.2f, 0.2f)
                    if (rhythmState.value.selectedMode == RhythmMode.RHYTHM) {
                        playbackParams = PlaybackParams().setSpeed(rhythmState.value.speedByBpm)
                    }
                }
                mediaPlayer.prepareAsync()
            }.onFailure { continuation.resumeWithException(it) }
        }
    }

    fun releaseMusicPlayers() {
        soundPool.release()
        mediaPlayer.release()
    }

    fun playMusic() {
        if (rhythmState.value.isPlayerLoaded) {
            Choreographer.getInstance().postFrameCallback {
                mediaPlayer.start()
                playOrResumeSoundPool()
            }
        } else {
            viewModelScope.launch {
                changeIsPlaying(PlayState.DEFAULT)
                _rhythmSideEffect.emit(RhythmSideEffect.ErrorToast)
            }
        }
    }

    private fun playOrResumeSoundPool() {
        if (beatStream != 0) {
            soundPool.resume(beatStream)
        } else {
            beatStream = soundPool.play(beatSound, 10f, 10f, 1, -1, 1f)
        }
    }

    fun pauseMusic(isDialogNeeded: Boolean) {
        Choreographer.getInstance().postFrameCallback {
            if (beatStream != 0) soundPool.pause(beatStream)
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
        }
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

    fun postRhythmRecordToSave(accuracy: Double) {
        viewModelScope.launch {
            rhythmRepository.postRhythmRecord(
                RecordRequestModel(
                    accuracy = accuracy,
                    steps = rhythmState.value.stepCount,
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

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
        mediaPlayer.release()
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