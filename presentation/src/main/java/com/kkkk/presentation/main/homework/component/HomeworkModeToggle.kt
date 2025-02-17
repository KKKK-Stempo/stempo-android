package com.kkkk.presentation.main.homework.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.homework.model.HomeworkMode
import com.kkkk.presentation.main.theme.Black
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent
import com.kkkk.presentation.main.theme.White

@Composable
fun HomeworkModeToggle(
    modifier: Modifier = Modifier,
    selectedMode: HomeworkMode = HomeworkMode.MYSELF,
    onToggleSelected: (HomeworkMode) -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(color = Purple50, shape = RoundedCornerShape(26.dp))
            .padding(2.dp)
            .clip(RoundedCornerShape(26.dp))
    ) {
        HomeworkModeToggleItem(
            mode = HomeworkMode.MYSELF,
            selectedMode = selectedMode,
            onToggleClick = onToggleSelected,
            textPadding = 15.dp
        )
        HomeworkModeToggleItem(
            mode = HomeworkMode.TEACHER,
            selectedMode = selectedMode,
            onToggleClick = onToggleSelected,
            textPadding = 9.dp
        )
    }
}

@Composable
fun HomeworkModeToggleItem(
    mode: HomeworkMode,
    selectedMode: HomeworkMode,
    onToggleClick: (HomeworkMode) -> Unit,
    textPadding: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(if (selectedMode == mode) White else Transparent)
            .clickableWithoutRipple { onToggleClick(mode) }
            .padding(vertical = 3.dp, horizontal = textPadding),
        contentAlignment = Alignment.Center
    ) {
       FixedText(
            text = mode.text,
            color = if (selectedMode == mode) Black else White,
            style = StempoTheme.typography.body3,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeworkModeTogglePreview() {

    StempoTheme {
        HomeworkModeToggle()
    }
}