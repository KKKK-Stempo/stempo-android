package com.kkkk.presentation.main.record

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.kkkk.presentation.main.record.component.DottedShape
import com.kkkk.presentation.main.record.component.RecordLineChart
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent50
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R
import okhttp3.internal.immutableListOf

@Composable
fun RecordRoute(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val lottieLoading by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.stempo_loading)
    )

    LaunchedEffect(viewModel.recordSideEffect, lifecycleOwner) {
        viewModel.recordSideEffect.collect { sideEffect ->
            when (sideEffect) {
                RecordSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
            }
        }
    }

    LaunchedEffect(recordState.isLoading) {
        systemUiController.setStatusBarColor(color = if (recordState.isLoading) Transparent50 else White)
    }

    LaunchedEffect(recordState.selectedMonth) {
        viewModel.updateIsLoading(true)
        viewModel.setGraphWithDate()
    }

    RecordScreen(
        recordState = recordState,
        lottieLoading = lottieLoading,
        onMonthChangeBtnClick = viewModel::changeIsDialogVisible,
        onDropdownItemClick = viewModel::updateSelectedMonth
    )
}

@Composable
private fun RecordScreen(
    recordState: RecordState,
    lottieLoading: LottieComposition? = null,
    onMonthChangeBtnClick: () -> Unit = {},
    onDropdownItemClick: (Int) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            RecordMonthSelectBtn(
                recordState = recordState,
                onMonthChangeBtnClick = onMonthChangeBtnClick,
                modifier = Modifier.padding(top = 24.dp)
            )

            RecordTitleWithAccuracy(
                recordState = recordState
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp)
                    .padding(horizontal = 5.dp),
            ) {
                RecordChartDashLine(
                    topPadding = 14.dp,
                    alignment = Alignment.TopCenter
                )
                RecordChartDashLine(
                    bottomPadding = 23.dp,
                    alignment = Alignment.Center
                )
                RecordChartDashLine(
                    bottomPadding = 37.dp,
                    alignment = Alignment.BottomCenter
                )
                RecordLineChart(
                    dateList = recordState.dateList,
                    entriesList = recordState.entriesList
                )

                if (recordState.isRecordEmpty) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.img_record_empty),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp)
                            .background(Gray100, RoundedCornerShape(8.dp))
                            .padding(vertical = 30.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                RecordAchievementTitle()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                        .background(color = Gray100, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    RecordBadge(
                        R.drawable.ic_badge_today,
                        R.string.report_tv_achieve_today,
                        recordState.countToday
                    )
                    RecordBadge(
                        R.drawable.ic_badge_week,
                        R.string.report_tv_achieve_week,
                        recordState.countWeek
                    )
                    RecordBadge(
                        R.drawable.ic_badge_sequence,
                        R.string.report_tv_achieve_consecutive,
                        recordState.countConsecutive
                    )
                }
            }
        }

        if (recordState.isDropDownVisible) {
            RecordDropDown(
                onDropdownItemClick = onDropdownItemClick
            )
        }

        if (recordState.isLoading) {
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
fun RecordMonthSelectBtn(
    recordState: RecordState,
    onMonthChangeBtnClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier.padding(start = 17.dp),
            text = stringResource(id = R.string.report_tv_month, recordState.selectedMonth),
            style = StempoTheme.typography.head1
        )
        IconButton(
            onClick = onMonthChangeBtnClick,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(44.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = if (recordState.isDropDownVisible) R.drawable.ic_drop_up else R.drawable.ic_drop_down),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun RecordTitleWithAccuracy(
    recordState: RecordState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(id = R.string.report_tv_title),
            style = StempoTheme.typography.head1
        )
        if (!recordState.isRecordEmpty) {
            Text(
                modifier = Modifier.padding(end = 20.dp),
                text = stringResource(
                    id = R.string.report_tv_accuracy_average,
                    recordState.averageAccuracy
                ),
                style = StempoTheme.typography.body1
            )
        }
    }
}

@Composable
fun RecordDropDown(
    modifier: Modifier = Modifier,
    onDropdownItemClick: (Int) -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(start = 16.dp, top = 70.dp)
            .shadow(10.dp, RoundedCornerShape(8.dp))
            .background(White, RoundedCornerShape(8.dp))
    ) {
        LazyColumn {
            items(immutableListOf(1, 3, 6), key = { it }) { month ->
                Text(
                    text = stringResource(id = R.string.report_tv_month, month),
                    style = StempoTheme.typography.body1,
                    modifier = Modifier
                        .clickableWithoutRipple { onDropdownItemClick(month) }
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 44.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun BoxScope.RecordChartDashLine(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp,
    alignment: Alignment = Alignment.Center
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = topPadding, bottom = bottomPadding)
            .height(1.dp)
            .align(alignment)
            .background(color = Gray300, shape = DottedShape(12.dp))
    )
}

@Composable
fun RecordBadge(
    @DrawableRes imageRes: Int,
    @StringRes text: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(imageRes),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = stringResource(text),
            style = StempoTheme.typography.caption1,
            color = Gray600
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(R.string.report_tv_achieve_count, count),
            style = StempoTheme.typography.body1
        )
    }
}

@Composable
fun RecordAchievementTitle(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_crown),
            contentDescription = null,
        )
        Text(
            text = stringResource(id = R.string.report_tv_achieve_title),
            style = StempoTheme.typography.head3,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    StempoTheme {
        RecordScreen(
            recordState = RecordState(isLoading = false)
        )
    }
}