package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentRhythmBinding

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm) {

    private val viewModel by activityViewModels<RhythmViewModel>()
    private var rhythmBottomSheet: RhythmBottomSheet? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initChangeLevelBtnListener()
        setCurrentLevel()
    }

    private fun initChangeLevelBtnListener() {
        binding.btnChangeLevel.setOnSingleClickListener {
            rhythmBottomSheet = RhythmBottomSheet()
            rhythmBottomSheet?.show(parentFragmentManager, BOTTOM_SHEET_CHANGE_LEVEL)
        }
    }

    private fun setCurrentLevel() {
        binding.tvRhythmLevel.text =
            getString(R.string.rhythm_tv_level, viewModel.rhythmLevel.value)
        val (textColor, background) = when (viewModel.rhythmLevel.value?.rem(3)) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        rhythmBottomSheet = null
    }

    companion object {
        private const val BOTTOM_SHEET_CHANGE_LEVEL = "BOTTOM_SHEET_CHANGE_LEVEL"
    }
}