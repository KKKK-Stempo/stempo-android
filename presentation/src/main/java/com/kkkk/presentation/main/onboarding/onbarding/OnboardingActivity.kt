package com.kkkk.presentation.main.onboarding.onbarding

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityOnboardingBinding

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private lateinit var timer: CountDownTimer
    private val viewModel by viewModels<OnboardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.findFragmentById(R.id.fcv_onboarding)
        observeOnboardingState()
    }

    private fun observeOnboardingState() {
        viewModel.state
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    OnboardingState.START -> showStartFragment()
                    OnboardingState.MEASURE -> showMeasureFragment()
                    OnboardingState.END -> showEndFragment()
                    OnboardingState.DONE -> navigateToMain()
                }
            }.launchIn(lifecycleScope)
    }


    private fun showStartFragment() {
        supportFragmentManager.commit {
            replace(R.id.fcv_onboarding, OnboardingStartFragment())
        }
    }

    private fun showMeasureFragment() {
        supportFragmentManager.commit {
            replace(R.id.fcv_onboarding, OnboardingMeasureFragment())
        }
        startTimer()
    }

    private fun showEndFragment() {
        supportFragmentManager.commit {
            replace(R.id.fcv_onboarding, OnboardingEndFragment())
        }
    }

    private fun navigateToMain() {
        navigateToScreenClear<OnboardingActivity>()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                viewModel.setState(OnboardingState.END)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}
