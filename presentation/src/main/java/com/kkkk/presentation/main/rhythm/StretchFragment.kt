package com.kkkk.presentation.main.rhythm

import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStretchBinding
import java.io.File
import kotlin.coroutines.resume

@AndroidEntryPoint
class StretchFragment : BaseFragment<FragmentStretchBinding>(R.layout.fragment_stretch) {

    private val viewModel by activityViewModels<RhythmViewModel>()
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

        setStatusBarColor(R.color.white)
        initRhythmNavigateBtnListener()
        initPlayBtnListener()
        initStopBtnListener()
        setMusicPlayer()
    }

    private fun initRhythmNavigateBtnListener() {
        binding.btnRhythmMode.setOnSingleClickListener {
            viewModel.navigateToStretchView(false)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnStretchPlay.setOnSingleClickListener {
            if (::soundPool.isInitialized && ::mediaPlayer.isInitialized && isLoaded) {
                lifecycleScope.launch {
                    playSoundPoolAndMediaPlayer()
                }
            }
        }
    }

    private suspend fun playSoundPoolAndMediaPlayer() {
        lifecycleScope.launch {
            listOf(
                async { playOrResumeSoundPool() },
                async { mediaPlayer.start() }
            ).awaitAll()
            switchPlayingState(true)
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun playOrResumeSoundPool() {
        if (beatStream != 0) {
            soundPool.resume(beatStream)
        } else {
            beatStream = soundPool.play(beatSound, 1f, 1f, 1, -1, 1f)
        }
    }

    private fun initStopBtnListener() {
        binding.btnStretchStop.setOnSingleClickListener {
            pauseMusic()
        }
    }

    override fun onStop() {
        super.onStop()
        pauseMusic()
    }

    private fun pauseMusic() {
        lifecycleScope.launch {
            listOf(
                async { if (beatStream != 0) soundPool.pause(beatStream) },
                async { mediaPlayer.pause() }
            ).awaitAll()
            switchPlayingState(false)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun switchPlayingState(start: Boolean) {
        with(binding) {
            btnStretchPlay.isVisible = !start
            btnStretchStop.isVisible = start
            lottieStretchBg.isVisible = start
        }
    }

    private fun setMusicPlayer() {
        lifecycleScope.launch {
            listOf(
                async { setSoundPoolAsync() },
                async { setMediaPlayerAsync() }
            ).awaitAll()
            isLoaded = true
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
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.music_stretch).apply {
            isLooping = true
            setOnPreparedListener {
                continuation.resume(Unit)
            }
        }
        continuation.invokeOnCancellation {
            if (::mediaPlayer.isInitialized) mediaPlayer.release()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        if (::soundPool.isInitialized) soundPool.release()
    }
}