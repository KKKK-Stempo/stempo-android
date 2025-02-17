package com.kkkk.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun OneButtonDialog(
    content: String = "",
    onConfirmClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FixedText(
                text = content,
                style = StempoTheme.typography.head3.copy(
                    lineHeight = 28.sp
                ),
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Purple50)
                    .clickableWithoutRipple { onConfirmClick() }
                    .padding(vertical = 15.dp, horizontal = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                FixedText(
                    text = "확인",
                    style = StempoTheme.typography.head4,
                    color = Purple10
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmSyncDialogPreview() {
    StempoTheme {
        OneButtonDialog(
            content = stringResource(R.string.rhythm_wearable_sync)
        )
    }
}