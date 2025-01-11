package com.kkkk.presentation.main.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.stempo.presentation.R

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(viewModel.profileSideEffect, lifecycleOwner) {
        viewModel.profileSideEffect.collect { sideEffect ->
            when (sideEffect) {
                ProfileSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
                ProfileSideEffect.NotPreparedToast -> context.toast(context.stringOf(R.string.profile_not_prepared))
            }
        }
    }

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(color = Gray100)
    }

    ProfileScreen(
        profileState = profileState,
        onClearBtnClick = { }
    )
}

@Composable
private fun ProfileScreen(
    profileState: ProfileState,
    onClearBtnClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Profile Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    StempoTheme {
        ProfileScreen(profileState = ProfileState())
    }
}