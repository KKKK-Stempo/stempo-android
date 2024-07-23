package com.kkkk.presentation.main.onboarding.onbarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.kkkk.core.base.BaseFragment
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentOnboardingMeasureBinding

class OnboardingMeasureFragment :
    BaseFragment<FragmentOnboardingMeasureBinding>(R.layout.fragment_onboarding_measure) {
    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_measure, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
