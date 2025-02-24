package com.kkkk.presentation.main.rhythm.manager

import android.content.Context
import android.media.SoundPool
import android.net.Uri
import android.view.Choreographer
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

    // 현재 로드된 비트(SoundPool)의 사운드 ID
    private var beatSound: Int = NO_SOUND

    // 현재 재생 중이거나 일시정지된 비트(SoundPool)의 스트림 ID
    private var beatStream: Int = NO_STREAM

    /**
     * 동시에 ExoPlayer와 SoundPool을 로드하여 리듬 및 비트 사운드를 초기화합니다.
     *
     * @param resourceId ExoPlayer가 사용할 raw 리소스의 리소스 ID.
     * @param speed ExoPlayer의 재생 속도 (기본값 1.0f).
     * @param file SoundPool이 사용할 사운드 파일.
     */
    suspend fun load(resourceId: Int, speed: Float = 1.0f, file: File) =
        coroutineScope {
            listOf(
                async { loadExoPlayerAsync(resourceId, speed) },
                async { loadSoundPoolAsync(file) }
            ).awaitAll()
        }

    private fun setupExoPlayerIfNeeded() {
        if (!::exoPlayer.isInitialized) {
            exoPlayer = ExoPlayer.Builder(appContext).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0.2f
            }
        }
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

    private fun setupSoundPoolIfNeeded() {
        if (!::soundPool.isInitialized) {
            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .build()
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

    /**
     * 시작 프레임 콜백에서 ExoPlayer를 재생 상태로 전환하고, SoundPool의 사운드를 재개하거나 새로 재생합니다.
     */
    fun play() {
        Choreographer.getInstance().postFrameCallback {
            exoPlayer.play()
            playOrResumeSoundPool()
        }
    }

    private fun playOrResumeSoundPool() {
        if (beatStream != 0) {
            soundPool.resume(beatStream)
        } else {
            beatStream = soundPool.play(beatSound, 10f, 10f, 1, -1, 1f)
        }
    }

    /**
     * 시작 프레임 콜백에서 SoundPool에 재생 중인 사운드가 있다면 일시정지시키고, ExoPlayer를 일시정지합니다.
     */
    fun pause() {
        Choreographer.getInstance().postFrameCallback {
            if (beatStream != NO_STREAM) soundPool.pause(beatStream)
            exoPlayer.pause()
        }
    }

    /**
     * ExoPlayer와 SoundPool에 할당된 리소스를 해제합니다.
     */
    fun release() {
        exoPlayer.release()
        soundPool.release()
    }

    companion object {
        private const val NO_SOUND = 0
        private const val NO_STREAM = 0
    }
}