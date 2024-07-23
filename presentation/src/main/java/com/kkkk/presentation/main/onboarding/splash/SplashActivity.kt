package com.kkkk.presentation.main.onboarding.splash

import android.os.Bundle
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.setNavigationBarColorFromResource
import com.kkkk.core.extension.setStatusBarColorFromResource
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivitySplashBinding

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor()
        setNavigationBarColor()
    }

    private fun setStatusBarColor() = setStatusBarColorFromResource(R.color.purple_50)
    private fun setNavigationBarColor() = setNavigationBarColorFromResource(R.color.purple_50)
}
