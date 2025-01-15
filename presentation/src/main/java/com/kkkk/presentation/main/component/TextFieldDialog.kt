package com.kkkk.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.Purple10
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R

@Composable
fun TextFieldDialog(
    title: String = "",
    onExitBtnClick: () -> Unit = {},
    onSaveBtnClick: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val textValue = rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = StempoTheme.typography.head3,
            )

            TextField(
                value = textValue.value,
                onValueChange = { newValue ->
                    textValue.value = newValue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = StempoTheme.typography.body2,
                placeholder = {
                    Text(
                        text = stringResource(R.string.study_add_hint),
                        style = StempoTheme.typography.body2,
                        color = Gray500
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent,
                    disabledIndicatorColor = Transparent,
                    errorIndicatorColor = Transparent,
                    focusedContainerColor = Gray100,
                    unfocusedContainerColor = Gray100,
                    disabledContainerColor = Gray100,
                    errorContainerColor = Gray100,
                    cursorColor = Purple50,
                )
            )

            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.study_add_exit),
                    style = StempoTheme.typography.head4,
                    color = Purple50,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Purple10)
                        .clickableWithoutRipple { onExitBtnClick() }
                        .padding(vertical = 15.dp)
                )
                Text(
                    text = stringResource(R.string.study_add_save),
                    style = StempoTheme.typography.head4,
                    color = Purple10,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Purple50)
                        .clickableWithoutRipple { onSaveBtnClick(textValue.value) }
                        .padding(vertical = 15.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldDialogPreview() {
    StempoTheme {
        TextFieldDialog(
            title = stringResource(R.string.study_add_title)
        )
    }
}