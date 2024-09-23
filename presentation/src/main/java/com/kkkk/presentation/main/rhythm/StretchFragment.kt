package com.kkkk.presentation.main.rhythm

import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.rhythm.RhythmFragment.Companion.findMusicByBpm
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStretchBinding
import java.io.File

@AndroidEntryPoint
class StretchFragment : BaseFragment<FragmentStretchBinding>(R.layout.fragment_stretch) {

    private val viewModel by activityViewModels<RhythmViewModel>()
    private lateinit var soundPool: SoundPool
    private var beatSound: Int = 0
    private var musicSound: Int = 0
    private var isSoundLoaded = false
    private var isPlayed = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initRhythmNavigateBtnListener()
        initPlayBtnListener()
        initStopBtnListener()
        setMediaPlayer()
    }

    private fun initRhythmNavigateBtnListener() {
        binding.btnRhythmMode.setOnSingleClickListener {
            viewModel.navigateToStretchView(false)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnStretchPlay.setOnSingleClickListener {
            if (::soundPool.isInitialized && isSoundLoaded) {
                if (!isPlayed) {
                    with(soundPool) {
                        play(musicSound, 1f, 1f, 1, -1, 1f)
                        play(beatSound, 1f, 1f, 1, -1, 1f)
                    }
                    isPlayed = true
                } else {
                    soundPool.autoResume()
                }
                switchPlayingState(true)
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    private fun initStopBtnListener() {
        binding.btnStretchStop.setOnSingleClickListener {
            if (::soundPool.isInitialized && isSoundLoaded) {
                soundPool.autoPause()
                switchPlayingState(false)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::soundPool.isInitialized && isSoundLoaded) {
            soundPool.autoPause()
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

    private fun setMediaPlayer() {
        if (File(requireContext().filesDir, viewModel.filename).exists()) {
            if (::soundPool.isInitialized) soundPool.release()
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            beatSound =
                soundPool.load(File(requireContext().filesDir, viewModel.filename).absolutePath, 1)
            musicSound = soundPool.load(requireContext(), findMusicByBpm(viewModel.bpm), 1)
            soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0 && (sampleId == musicSound || sampleId == beatSound)) {
                    isSoundLoaded = true
                }
            }
        } else {
            toast(stringOf(R.string.error_msg))
        }
    }
}