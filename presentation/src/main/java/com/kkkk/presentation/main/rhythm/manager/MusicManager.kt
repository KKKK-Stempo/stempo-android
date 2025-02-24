package com.kkkk.presentation.main.rhythm.manager

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context
    private var exoPlayer: ExoPlayer? = null

    private fun setupExoPlayerIfNeeded() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(appContext).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0.2f
            }
        }
    }

    suspend fun loadNewMediaAsync(resourceId: Int, speed: Float = 1.0f) =
        suspendCancellableCoroutine { continuation ->
            runCatching {
                setupExoPlayerIfNeeded()

                // 기존 음원 제거 후 새로운 음원 로드
                exoPlayer?.clearMediaItems()
                val uri: Uri = Uri.parse("android.resource://${appContext.packageName}/$resourceId")
                val mediaItem = MediaItem.fromUri(uri)
                exoPlayer?.setMediaItem(mediaItem)

                // 재생 속도 재설정
                exoPlayer?.playbackParameters = PlaybackParameters(speed)

                // 로드 성공 or 실패 감지 리스너 설정 및 제거
                exoPlayer?.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            exoPlayer?.removeListener(this)
                            continuation.resume(Unit)
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        exoPlayer?.removeListener(this)
                        continuation.resumeWithException(IllegalStateException(error))
                    }
                })

                exoPlayer?.prepare()
            }.onFailure { continuation.resumeWithException(it) }
        }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}