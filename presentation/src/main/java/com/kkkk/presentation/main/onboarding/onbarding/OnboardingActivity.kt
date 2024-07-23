package com.kkkk.presentation.main.onboarding.onbarding

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.presentation.main.MainActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityOnboardingBinding

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private lateinit var timer: CountDownTimer
    private val viewModel by viewModels<OnboardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFragmentManager()
        observeOnboardingState()
    }

    private fun initFragmentManager() {
        supportFragmentManager.findFragmentById(R.id.fcv_onboarding)
    }

    private fun observeOnboardingState() {
        viewModel.state
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    OnboardingState.START -> navigateTo<OnboardingStartFragment>()
                    OnboardingState.MEASURE -> navigateTo<OnboardingMeasureFragment>()
                    OnboardingState.END -> navigateTo<OnboardingEndFragment>()
                    OnboardingState.DONE -> navigateToScreenClear<MainActivity>()
                }
            }.launchIn(lifecycleScope)
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

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_onboarding, T::class.java.canonicalName)
        }
    }
}
