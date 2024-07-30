package com.kkkk.presentation.onboarding.onbarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentOnboardingEndBinding
import kr.genti.presentation.databinding.FragmentOnboardingStartBinding

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
