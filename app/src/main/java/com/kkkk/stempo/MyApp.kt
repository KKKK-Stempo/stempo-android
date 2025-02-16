package com.kkkk.stempo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kkkk.presentation.manager.AmplitudeManager
import com.kkkk.stempo.BuildConfig.AMPLITUDE_KEY
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initTimber()
        initAmplitude()
        setDayMode()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun initAmplitude() {
        AmplitudeManager.init(this, AMPLITUDE_KEY)
    }

    private fun setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}