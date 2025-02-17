package com.kkkk.presentation.main.rhythm.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.rhythm.model.RhythmMode
import com.kkkk.presentation.main.theme.Black
import com.kkkk.presentation.main.theme.Gray200
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.Transparent
import com.kkkk.presentation.main.theme.White


@Composable
fun RhythmModeToggle(
    modifier: Modifier = Modifier,
    selectedMode: RhythmMode = RhythmMode.RHYTHM,
    onToggleSelected: (RhythmMode) -> Unit
) {
    Row(
        modifier = modifier
            .background(color = Gray200, shape = RoundedCornerShape(26.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(26.dp))
    ) {
        RhythmModeToggleItem(
            mode = RhythmMode.RHYTHM,
            selectedMode = selectedMode,
            onToggleClick = onToggleSelected,
            modifier = Modifier.weight(1f)
        )
        RhythmModeToggleItem(
            mode = RhythmMode.STRETCH,
            selectedMode = selectedMode,
            onToggleClick = onToggleSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RhythmModeToggleItem(
    mode: RhythmMode,
    selectedMode: RhythmMode,
    onToggleClick: (RhythmMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(if (selectedMode == mode) White else Transparent)
            .clickableWithoutRipple { onToggleClick(mode) }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        FixedText(
            text = mode.text,
            color = if (selectedMode == mode) Black else Gray600,
            style = StempoTheme.typography.head3,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmModeTogglePreview() {
    var selectedMode by remember { mutableStateOf(RhythmMode.RHYTHM) }

    StempoTheme {
        RhythmModeToggle(
            selectedMode = selectedMode,
            onToggleSelected = { selectedMode = it }
        )
    }
}