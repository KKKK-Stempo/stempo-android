package com.kkkk.presentation.main.rhythm

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStretchBinding
import java.io.File

@AndroidEntryPoint
class StretchFragment : BaseFragment<FragmentStretchBinding>(R.layout.fragment_stretch) {

    private val viewModel by activityViewModels<RhythmViewModel>()
    private lateinit var mediaPlayer: MediaPlayer

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
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.start()
                switchPlayingState(true)
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }
    }

    private fun initStopBtnListener() {
        binding.btnStretchStop.setOnSingleClickListener {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.pause()
                switchPlayingState(false)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
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
        if (File(requireContext().filesDir, STRETCH_WAV_FILE).exists()) {
            if (::mediaPlayer.isInitialized) mediaPlayer.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    File(requireContext().filesDir, STRETCH_WAV_FILE).absolutePath
                )
                prepare()
            }
        } else {
            toast(stringOf(R.string.error_msg))
        }
    }

    companion object {
        const val STRETCH_WAV_FILE = "stempo_bpm_65_bit_2"
    }
}