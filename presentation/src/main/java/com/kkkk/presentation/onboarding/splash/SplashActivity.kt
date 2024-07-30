package com.kkkk.presentation.onboarding.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.core.extension.setNavigationBarColorFromResource
import com.kkkk.core.extension.setStatusBarColorFromResource
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.MainActivity
import com.kkkk.presentation.main.onboarding.onbarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivitySplashBinding

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor()
        setNavigationBarColor()
        observeStates()

        viewModel.checkTokenState()
    }

    private fun setStatusBarColor() = setStatusBarColorFromResource(R.color.purple_50)
    private fun setNavigationBarColor() = setNavigationBarColorFromResource(R.color.purple_50)

    private fun observeStates() {
        observeTokenState()
        observeUserState()
    }

    private fun observeTokenState() {
        viewModel.isValidToken.flowWithLifecycle(lifecycle).onEach { isValidToken ->
            if (isValidToken) {
                navigateToScreenClear<MainActivity>()
            } else {
                login()
            }
        }.launchIn(lifecycleScope)
    }

    private fun observeUserState() {
        viewModel.userState.flowWithLifecycle(lifecycle).onEach { isSuccess ->
            if (isSuccess) {
                navigateToScreenClear<OnboardingActivity>()
            } else {
                toast(getString(R.string.error_msg))
            }
        }.launchIn(lifecycleScope)
    }

    private fun login() {
        viewModel.setAndroidId(getDeviceTag())
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceTag(): String =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}
