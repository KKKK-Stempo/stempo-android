package com.kkkk.presentation.main.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.kkkk.presentation.main.rhythm.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Black
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent
import com.kkkk.stempo.presentation.R

@Composable
fun RecordRoute(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val recordState by viewModel.recordState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

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
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(start = 18.dp),
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
                    modifier = Modifier.padding(start = 18.dp),
                    text = stringResource(id = R.string.report_tv_title),
                    style = StempoTheme.typography.head1
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp, end = 18.dp),
                    text = stringResource(id = R.string.report_tv_accuracy_average, recordState.averageAccuracy),
                    style = StempoTheme.typography.body1
                )
            }
        }
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