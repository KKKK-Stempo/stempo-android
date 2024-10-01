package com.kkkk.presentation.onboarding.splash

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.core.extension.setNavigationBarColorFromResource
import com.kkkk.core.extension.setStatusBarColorFromResource
import com.kkkk.presentation.main.MainActivity
import com.kkkk.presentation.onboarding.onbarding.OnboardingActivity
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor()
        setNavigationBarColor()
        observeStates()
    }

    override fun onResume() {
        super.onResume()
        if (isActivityRecognitionPermissionGranted(this)) {
            viewModel.checkTokenState()
        } else {
            showDialog()
        }
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
                navigateToScreenClear<MainActivity>()
            } else {
                navigateToScreenClear<OnboardingActivity>()
            }
        }.launchIn(lifecycleScope)
    }

    private fun login() {
        viewModel.setAndroidId(getDeviceTag())
    }

    private fun isActivityRecognitionPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android Q 미만 버전에서는 이 권한이 필요하지 않으므로 항상 true 반환
            true
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_single_button)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        dialog.findViewById<View>(R.id.btn_onboarding_start_measure).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    200
                )
            } else {
                navigateToSettings()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.ACTIVITY_RECOGNITION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.checkTokenState()
                } else {
                    navigateToSettings()
                }
            }
        }
    }

    private fun navigateToSettings() {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceTag(): String =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}
