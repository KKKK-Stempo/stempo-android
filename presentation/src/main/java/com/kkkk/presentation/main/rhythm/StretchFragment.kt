package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStretchBinding

@AndroidEntryPoint
class StretchFragment : BaseFragment<FragmentStretchBinding>(R.layout.fragment_stretch) {

    private val viewModel by activityViewModels<RhythmViewModel>()
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initRhythmNavigateBtnListener()
    }

    private fun initRhythmNavigateBtnListener() {
        binding.btnRhythm.setOnSingleClickListener {
            viewModel.navigateToStretchView(false)
        }
    }

}