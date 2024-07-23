package com.kkkk.presentation.main.onboarding.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.core.extension.setNavigationBarColorFromResource
import com.kkkk.core.extension.setStatusBarColorFromResource
import com.kkkk.presentation.main.MainActivity
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
        observeUserState()
    }

    private fun setStatusBarColor() = setStatusBarColorFromResource(R.color.purple_50)
    private fun setNavigationBarColor() = setNavigationBarColorFromResource(R.color.purple_50)

    private fun observeUserState() {
        viewModel.userState.flowWithLifecycle(lifecycle).onEach { state ->
            navigateToScreenClear<MainActivity>()
        }.launchIn(lifecycleScope)
    }
}
