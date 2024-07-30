package com.kkkk.presentation.main.rhythm

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.core.state.UiState
import com.kkkk.presentation.main.rhythm.RhythmViewModel.Companion.LEVEL_UNDEFINED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentRhythmBinding
import java.io.File
import java.nio.file.Files

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm) {

    private val viewModel by activityViewModels<RhythmViewModel>()
    private var rhythmBottomSheet: RhythmBottomSheet? = null
    private lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initChangeLevelBtnListener()
        initPlayBtnListener()
        initStopBtnListener()
        observeRhythmLevel()
        observeRhythmUrlState()
        observeDownloadState()
    }

    private fun initChangeLevelBtnListener() {
        binding.btnChangeLevel.setOnSingleClickListener {
            rhythmBottomSheet = RhythmBottomSheet()
            rhythmBottomSheet?.show(parentFragmentManager, BOTTOM_SHEET_CHANGE_LEVEL)
        }
    }

    private fun initPlayBtnListener() {
        binding.btnRhythmPlay.setOnSingleClickListener {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.start()
                switchPlayingState(true)
            } else {
                toast(stringOf(R.string.error_msg))
            }
        }
    }

    private fun initStopBtnListener() {
        binding.btnRhythmStop.setOnSingleClickListener {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.pause()
                switchPlayingState(false)
            }
        }
    }

    private fun switchPlayingState(start: Boolean) {
        with(binding) {
            btnRhythmPlay.isVisible = !start
            btnRhythmStop.isVisible = start
        }
    }

    private fun observeRhythmLevel() {
        viewModel.rhythmLevel.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { level ->
            if (level == LEVEL_UNDEFINED) return@onEach
            setLoadingView(true)
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.pause()
                switchPlayingState(false)
            }
            setUiWithCurrentLevel()
            viewModel.postToGetRhythmUrlFromServer(level)
        }.launchIn(lifecycleScope)
    }

    private fun setUiWithCurrentLevel() {
        binding.tvRhythmLevel.text =
            getString(R.string.rhythm_tv_level, viewModel.rhythmLevel.value)
        val (textColor, background) = when (viewModel.rhythmLevel.value.rem(3)) {
            1 -> Pair(R.color.purple_50, R.drawable.shape_purple50_line_17_rect)
            2 -> Pair(R.color.sky_50, R.drawable.shape_sky50_line_17_rect)
            0 -> Pair(R.color.green_50, R.drawable.shape_green50_line_17_rect)
            else -> return
        }
        with(binding) {
            tvRhythmLevel.setTextColor(colorOf(textColor))
            tvRhythmLevel.background =
                ContextCompat.getDrawable(requireContext(), background)
            tvRhythmStep.setTextColor(colorOf(textColor))
            tvRhythmStep.background =
                ContextCompat.getDrawable(requireContext(), background)
        }
    }

    private fun observeRhythmUrlState() {
        viewModel.rhythmUrlState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (File(requireContext().filesDir, viewModel.filename).exists()) {
                            setMediaPlayer()
                        } else {
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
        }
            .onSuccess {
                setMediaPlayer()
            }
            .onFailure {
                toast(stringOf(R.string.error_msg))
            }
    }

    private fun setMediaPlayer() {
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(
                File(
                    requireContext().filesDir,
                    viewModel.filename
                ).absolutePath
            )
            prepare()
        }
        setLoadingView(false)
    }

    private fun setLoadingView(isLoading: Boolean) {
        binding.layoutLoading.isVisible = isLoading
        if (isLoading) {
            setStatusBarColor(R.color.transparent_50)
        } else {
            setStatusBarColor(R.color.white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rhythmBottomSheet = null
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    companion object {
        private const val BOTTOM_SHEET_CHANGE_LEVEL = "BOTTOM_SHEET_CHANGE_LEVEL"
    }
}