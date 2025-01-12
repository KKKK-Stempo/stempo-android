package com.kkkk.presentation.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.rhythm.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray200
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
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
        versionText = "",
        onAnnounceBtnClick = { },
        onFaqBtnClick = { },
        onVoiceBtnClick = { },
        onSuggestBtnClick = { },
        onWithdrawBtnClick = { }
    )
}

@Composable
private fun ProfileScreen(
    profileState: ProfileState,
    versionText: String = "",
    onAnnounceBtnClick: () -> Unit = {},
    onFaqBtnClick: () -> Unit = {},
    onVoiceBtnClick: () -> Unit = {},
    onSuggestBtnClick: () -> Unit = {},
    onWithdrawBtnClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(Gray100)
            .fillMaxSize(),
    ) {
        Column {
            ProfileTitle()

            ProfileContentBox(
                firstItemText = stringResource(id = R.string.profile_btn_announce),
                onFirstItemClick = onAnnounceBtnClick,
                secondItemText = stringResource(id = R.string.profile_btn_faq),
                onSecondItemClick = onFaqBtnClick,
            )

            ProfileContentBox(
                firstItemText = stringResource(id = R.string.profile_btn_voice),
                onFirstItemClick = onVoiceBtnClick,
                secondItemText = stringResource(id = R.string.profile_btn_suggest),
                onSecondItemClick = onSuggestBtnClick,
            )

            ProfileContentBox(
                firstItemText = stringResource(id = R.string.profile_version),
                versionText = versionText,
                secondItemText = stringResource(id = R.string.profile_btn_withdraw),
                onSecondItemClick = onWithdrawBtnClick,
            )
        }
    }
}

@Composable
fun ProfileTitle(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = 16.dp, top = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_logo_purple),
            contentDescription = null,
        )
        Text(
            text = stringResource(id = R.string.profile_tv_title),
            style = StempoTheme.typography.head2,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ProfileContentBox(
    modifier: Modifier = Modifier,
    firstItemText: String = "",
    onFirstItemClick: () -> Unit = {},
    versionText: String = "",
    secondItemText: String = "",
    onSecondItemClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .background(White, RoundedCornerShape(8.dp))
            .border(1.dp, Gray200, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        ProfileContentItem(
            text = firstItemText,
            versionText = versionText,
            onClick = onFirstItemClick
        )
        ProfileContentItem(
            text = secondItemText,
            onClick = onSecondItemClick
        )
    }
}

@Composable
fun ProfileContentItem(
    modifier: Modifier = Modifier,
    text: String = "",
    versionText: String = "",
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(vertical = 18.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .clickableWithoutRipple { if (versionText.isNotEmpty()) onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = StempoTheme.typography.head4
        )
        if (versionText.isEmpty()) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_next_gray),
                contentDescription = null
            )
        } else {
            Text(
                text = versionText,
                style = StempoTheme.typography.body2,
                color = Gray500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    StempoTheme {
        ProfileScreen(profileState = ProfileState(), versionText = "v1.0.0")
    }
}