package com.kkkk.presentation.main.onboarding.onbarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentOnboardingMeasureBinding

class OnboardingMeasureFragment :
    BaseFragment<FragmentOnboardingMeasureBinding>(R.layout.fragment_onboarding_measure) {
    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtonListener()
    }

    private fun initButtonListener() {
        with(binding) {
            btnOnboardingMeasureLater.setOnClickListener {
                viewModel.setState(OnboardingState.DONE)
            }
        }
    }
}
