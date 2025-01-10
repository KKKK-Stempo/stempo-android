package com.kkkk.presentation.main.record

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.core.extension.stringOf
import com.kkkk.core.extension.toast
import com.kkkk.presentation.main.record.component.DottedShape
import com.kkkk.presentation.main.record.component.RecordLineChart
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.stempo.presentation.R

@Composable
fun RecordRoute(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(viewModel.recordSideEffect, lifecycleOwner) {
        viewModel.recordSideEffect.collect { sideEffect ->
            when (sideEffect) {
                RecordSideEffect.ErrorToast -> context.toast(context.stringOf(R.string.error_msg))
            }
        }
    }

    RecordScreen(
        recordState = recordState,
        onMonthChangeBtnClick = {}
    )
}

@Composable
private fun RecordScreen(
    recordState: RecordState,
    onMonthChangeBtnClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
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
                        imageVector = ImageVector.vectorResource(id = if (recordState.isDialogVisible) R.drawable.ic_drop_up else R.drawable.ic_drop_down),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(id = R.string.report_tv_title),
                    style = StempoTheme.typography.head1
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp, end = 16.dp),
                    text = stringResource(
                        id = R.string.report_tv_accuracy_average,
                        recordState.averageAccuracy
                    ),
                    style = StempoTheme.typography.body1
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp)
                    .padding(horizontal = 5.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp)
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                        .background(color = Gray300, shape = DottedShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 23.dp)
                        .height(1.dp)
                        .align(Alignment.Center)
                        .background(color = Gray300, shape = DottedShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 37.dp)
                        .height(1.dp)
                        .align(Alignment.BottomCenter)
                        .background(color = Gray300, shape = DottedShape(12.dp))
                )

                RecordLineChart(
                    dateList = recordState.dateList,
                    entriesList = recordState.entriesList
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp),
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
    }
}

@Composable
fun RecordBadge(
    @DrawableRes imageRes: Int,
    @StringRes text: Int,
    count: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(imageRes),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
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

@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    StempoTheme {
        RecordScreen(
            recordState = RecordState()
        )
    }
}