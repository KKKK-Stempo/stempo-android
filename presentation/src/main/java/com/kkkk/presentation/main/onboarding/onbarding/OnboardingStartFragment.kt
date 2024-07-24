package com.kkkk.presentation.main.onboarding.onbarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentOnboardingStartBinding

class OnboardingStartFragment :
    BaseFragment<FragmentOnboardingStartBinding>(R.layout.fragment_onboarding_start) {
    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtonListener()
    }

    private fun initButtonListener() {
        with(binding) {
            btnOnboardingStartLater.setOnClickListener {
                viewModel.setState(OnboardingState.DONE)
            }
            btnOnboardingStartMeasure.setOnClickListener {
                viewModel.setState(OnboardingState.MEASURE)
            }
        }
    }
}
