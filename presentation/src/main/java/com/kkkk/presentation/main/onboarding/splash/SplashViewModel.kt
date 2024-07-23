package com.kkkk.presentation.main.onboarding.splash

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(

) : ViewModel() {
    private val _userState = MutableSharedFlow<Boolean>()
    val userState: SharedFlow<Boolean>
        get() = _userState

    init {
        initSplash()
    }

    fun initSplash() {
        viewModelScope.launch {
            delay(DELAY_TIME)
            _userState.emit(true) // TODO: 추후 검증 로직이 생길 경우 변경 예정
        }
    }

    companion object {
        private const val DELAY_TIME = 2000L
    }
}
