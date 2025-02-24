package com.kkkk.presentation.main.rhythm.manager

import android.content.Context
import android.media.SoundPool
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var soundPool: SoundPool

    // 현재 로드된 음원의 사운드 ID
    private var beatSound: Int = NO_SOUND

    // 현재 재생 중이거나 일시정지된 음원의 스트림 ID
    private var beatStream: Int = NO_STREAM

    private fun setupExoPlayerIfNeeded() {
        if (!::exoPlayer.isInitialized) {
            exoPlayer = ExoPlayer.Builder(appContext).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0.2f
            }
        }
    }

    private fun setupSoundPoolIfNeeded() {
        if (!::soundPool.isInitialized) {
            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .build()
        }
    }

    suspend fun loadRhythmAndBeatPlayer(resourceId: Int, file: File, speed: Float = 1.0f) =
        coroutineScope {
            listOf(
                async { loadExoPlayerAsync(resourceId, speed) },
                async { loadSoundPoolAsync(file) }
            ).awaitAll()
        }

    private suspend fun loadExoPlayerAsync(resourceId: Int, speed: Float) =
        suspendCancellableCoroutine { continuation ->
            runCatching {
                setupExoPlayerIfNeeded()
                exoPlayer.apply {
                    // 기존 음원 제거 후 새로운 음원 로드
                    clearMediaItems()
                    setMediaItem(
                        MediaItem.fromUri(
                            Uri.parse("android.resource://${appContext.packageName}/$resourceId")
                        )
                    )

                    // 재생 속도 재설정
                    playbackParameters = PlaybackParameters(speed)

                    // 로드 성공 or 실패 감지 리스너 설정 및 제거
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_READY) {
                                removeListener(this)
                                continuation.resume(Unit)
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            removeListener(this)
                            continuation.resumeWithException(IllegalStateException(error))
                        }
                    })
                    prepare()
                }
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }

    private suspend fun loadSoundPoolAsync(file: File) =
        suspendCancellableCoroutine { continuation ->
            runCatching {
                setupSoundPoolIfNeeded()

                // 기존 음원 제거
                if (beatSound != NO_SOUND) soundPool.unload(beatSound)

                // 로드 성공 or 실패 감지 리스너 설정 및 제거
                soundPool.setOnLoadCompleteListener { spInner, sampleId, status ->
                    spInner.setOnLoadCompleteListener(null)
                    if (sampleId == beatSound) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(IllegalStateException(status.toString()))
                    }
                }

                // 새로운 음원 로드
                beatStream = NO_STREAM
                beatSound = soundPool.load(file.absolutePath, 1)
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun release() {
        exoPlayer.release()
        soundPool.release()
    }

    companion object {
        private const val NO_SOUND = 0
        private const val NO_STREAM = 0
    }
}