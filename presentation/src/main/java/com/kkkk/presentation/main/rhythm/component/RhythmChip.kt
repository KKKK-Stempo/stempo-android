package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White

@Composable
fun RhythmChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Gray600,
    isFilled: Boolean = false,
) {
    Box(
        modifier = modifier
            .background(
                color = if (isFilled) color else White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        FixedText(
            text = text,
            color = if (isFilled) White else color,
            style = StempoTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmChipPreview() {
    StempoTheme {
        RhythmChip(
            text = "2비트",
            color = Purple50
        )
    }
}