package com.kkkk.presentation.main.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.homework.component.HomeworkModeToggle
import com.kkkk.presentation.main.homework.component.HomeworkProgressBar
import com.kkkk.presentation.main.homework.model.HomeworkMode
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.stempo.presentation.R

@Composable
fun HomeworkRoute(
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val homeworkState by viewModel.homeworkState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(viewModel.homeworkSideEffect, lifecycleOwner) {
        viewModel.homeworkSideEffect.collect { sideEffect ->
            when (sideEffect) {
                HomeworkSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
            }
        }
    }

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(color = Gray100)
    }

    HomeworkScreen(
        homeworkState = homeworkState,
        onToggleSelected = viewModel::changeSelectedMode
    )
}

@Composable
private fun HomeworkScreen(
    homeworkState: HomeworkState,
    onToggleSelected: (HomeworkMode) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(Gray100)
            .fillMaxSize(),
    ) {
        Column {
            HomeworkTopContent(
                homeworkState = homeworkState,
                onToggleSelected = onToggleSelected
            )

            Spacer(modifier = Modifier.padding(top = 24.dp))

            HomeworkProgressBar(
                homeworkState = homeworkState
            )

            Spacer(modifier = Modifier.padding(top = 24.dp))

        }
    }
}

@Composable
fun HomeworkTopContent(
    homeworkState: HomeworkState,
    modifier: Modifier = Modifier,
    onToggleSelected: (HomeworkMode) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.study_tv_title),
            style = StempoTheme.typography.head2,
            modifier = Modifier.padding(start = 8.dp)
        )
        HomeworkModeToggle(
            selectedMode = homeworkState.selectedMode,
            onToggleSelected = onToggleSelected
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeworkScreenPreview() {
    StempoTheme {
        HomeworkScreen(
            homeworkState = HomeworkState()
        )
    }
}