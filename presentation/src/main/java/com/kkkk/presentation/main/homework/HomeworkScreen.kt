package com.kkkk.presentation.main.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.presentation.main.component.TextFieldDialog
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.homework.component.HomeworkModeToggle
import com.kkkk.presentation.main.homework.component.HomeworkProgressBarBox
import com.kkkk.presentation.main.homework.component.HomeworkTaskList
import com.kkkk.presentation.main.homework.model.HomeworkMode
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent50
import com.kkkk.stempo.presentation.R

@Composable
fun HomeworkRoute(
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val homeworkState by viewModel.homeworkState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val lottieLoading by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.stempo_loading)
    )

    LaunchedEffect(viewModel.homeworkSideEffect, lifecycleOwner) {
        viewModel.homeworkSideEffect.collect { sideEffect ->
            when (sideEffect) {
                HomeworkSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
                HomeworkSideEffect.SuccessAddToast -> context.toast(context.stringOf(R.string.study_toast_add))
                HomeworkSideEffect.SuccessDeleteToast -> context.toast(context.stringOf(R.string.study_toast_delete))
            }
        }
    }

    LaunchedEffect(homeworkState.isLoading) {
        systemUiController.setStatusBarColor(color = if (homeworkState.isLoading) Transparent50 else Gray100)
    }

    HomeworkScreen(
        homeworkState = homeworkState,
        lottieLoading = lottieLoading,
        onToggleSelected = viewModel::changeSelectedMode,
        onCheckedBtnClick = { viewModel.updateHomework(it.id, it.description, !it.completed) },
        onDeleteBtnClick = { viewModel.deleteHomework(it.id) },
        onAddBtnClick = { viewModel.changeDialogVisible(true) }
    )

    if (homeworkState.isDialogVisible) {
        TextFieldDialog(
            title = stringResource(R.string.study_add_title),
            onDismissRequest = { viewModel.changeDialogVisible(false) },
            onExitBtnClick = { viewModel.changeDialogVisible(false) },
            onSaveBtnClick = { viewModel.addHomework(it) }
        )
    }
}

@Composable
private fun HomeworkScreen(
    homeworkState: HomeworkState,
    lottieLoading: LottieComposition? = null,
    onToggleSelected: (HomeworkMode) -> Unit = {},
    onCheckedBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
    onDeleteBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
    onAddBtnClick: () -> Unit = {}
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

            Spacer(modifier = Modifier.height(24.dp))

            HomeworkProgressBarBox(
                homeworkState = homeworkState
            )

            Spacer(modifier = Modifier.height(24.dp))

            HomeworkTaskList(
                homeworkState = homeworkState,
                onCheckedBtnClick = onCheckedBtnClick,
                onDeleteBtnClick = onDeleteBtnClick,
                onAddBtnClick = onAddBtnClick
            )

        }

        if (homeworkState.isLoading) {
            LottieAnimation(
                composition = lottieLoading,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Transparent50)
                    .padding(horizontal = 50.dp)
                    .clickableWithoutRipple { }
            )
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