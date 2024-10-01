package com.kkkk.presentation.onboarding.onbarding

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CombinedVibration
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.presentation.main.MainActivity
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private lateinit var timer: CountDownTimer
    private val vibrator by lazy {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S) {
            this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        } else{
            this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        }
    }

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
                    OnboardingState.MEASURE -> {
                        navigateTo<OnboardingMeasureFragment>()
                        startTimer()
                    }

                    OnboardingState.END -> {
                        vibrate()
                        navigateTo<OnboardingEndFragment>()
                    }
                    OnboardingState.DONE -> navigateToScreenClear<MainActivity>()
                }
            }.launchIn(lifecycleScope)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(TIME, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                with(viewModel) {
                    setBpmLevel(getDeviceTag())
                    setState(OnboardingState.END)
                }
            }
        }.start()
    }

    private fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S) {
            val effect = VibrationEffect.createOneShot(VIBRATION_TIME, VIBRATION_AMPLITUDE)
            (vibrator as Vibrator).vibrate(effect)
        } else {
            val vibrationEffect = VibrationEffect.createOneShot(VIBRATION_TIME, VIBRATION_AMPLITUDE)
            val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
            (vibrator as VibratorManager).vibrate(combinedVibration)
        }
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

    @SuppressLint("HardwareIds")
    private fun getDeviceTag(): String =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    companion object {
        private const val TIME = 6000L
        private const val INTERVAL = 1000L
        private const val VIBRATION_TIME = 2000L
        private const val VIBRATION_AMPLITUDE = 200
    }
}
