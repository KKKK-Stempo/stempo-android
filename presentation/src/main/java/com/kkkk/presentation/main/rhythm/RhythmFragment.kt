package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import com.kkkk.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentRhythmBinding

@AndroidEntryPoint
class RhythmFragment : BaseFragment<FragmentRhythmBinding>(R.layout.fragment_rhythm) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

    }
}