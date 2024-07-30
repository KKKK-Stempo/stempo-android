package com.kkkk.presentation.onboarding.onbarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseFragment
import com.kkkk.presentation.onboarding.onbarding.OnboardingViewModel.Companion.SPEED_CALC_INTERVAL
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentOnboardingMeasureBinding

class OnboardingMeasureFragment :
    BaseFragment<FragmentOnboardingMeasureBinding>(R.layout.fragment_onboarding_measure),
    SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtonListener()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() { // 권한 요청은 미리 하기!! 허용 되기 전 측정하니까 측정 안댐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    200
                )
            } else {
                initializeSensor()
            }
        } else {
            initializeSensor()
        }
    }

    private fun initializeSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensor()
            } else {
                // 사용자가 권한을 거부한 경우 처리
                // 예: 사용자에게 권한의 필요성을 설명하는 다이얼로그 표시
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun initButtonListener() {
        with(binding) {
            btnOnboardingMeasureLater.setOnClickListener {
                viewModel.setState(OnboardingState.DONE)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            viewModel.addStepCount(1)

            if (viewModel.stepCount.value % SPEED_CALC_INTERVAL == 0) {
                calculateSpeed()
            }
        }
    }

    private fun calculateSpeed() {
        val currentTime = System.currentTimeMillis()
        val lastStepTime = viewModel.lastStepTime.value
        if (lastStepTime != 0L) {
            val timeDiff = currentTime - lastStepTime
            val speed = (SPEED_CALC_INTERVAL / (timeDiff / 1000f)) * 60 // 분당 걸음 수

            viewModel.setSpeed(speed)
        }
        viewModel.setLastStepTime(currentTime)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
