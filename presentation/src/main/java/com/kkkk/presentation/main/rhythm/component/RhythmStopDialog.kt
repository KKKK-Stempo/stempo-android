package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kkkk.presentation.main.theme.Purple10
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R

@Composable
fun RhythmStopDialog(
    onSaveClick: () -> Unit,
    onPauseClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rhythm_stop_tv_title),
                style = StempoTheme.typography.head3.copy(
                    lineHeight = 28.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .background(color = Purple10, shape = RoundedCornerShape(12.dp))
                        .padding(vertical = 15.dp)
                        .weight(1f)
                        .clickableWithoutRipple { onSaveClick() },
                    text = stringResource(R.string.rhythm_stop_btn_save),
                    style = StempoTheme.typography.head4,
                    textAlign = TextAlign.Center,
                    color = Purple50
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .background(color = Purple50, shape = RoundedCornerShape(12.dp))
                        .padding(vertical = 15.dp)
                        .weight(1f)
                        .clickableWithoutRipple { onPauseClick() },
                    text = stringResource(R.string.rhythm_stop_btn_pause),
                    style = StempoTheme.typography.head4,
                    textAlign = TextAlign.Center,
                    color = Purple10
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmStopDialogPreview() {
    StempoTheme {
        RhythmStopDialog(
            onSaveClick = {},
            onPauseClick = {},
            onDismissRequest = {}
        )
    }
}