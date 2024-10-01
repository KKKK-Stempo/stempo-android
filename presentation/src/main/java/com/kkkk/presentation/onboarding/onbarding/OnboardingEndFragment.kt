package com.kkkk.presentation.onboarding.onbarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.FragmentOnboardingEndBinding

class OnboardingEndFragment :
    BaseFragment<FragmentOnboardingEndBinding>(R.layout.fragment_onboarding_end) {
    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtonListener()
    }

    private fun initButtonListener() {
        with(binding) {
            btnOnboardingEndStart.setOnClickListener {
                viewModel.setState(OnboardingState.DONE)
            }
        }
    }
}
