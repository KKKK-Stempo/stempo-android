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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White

@Composable
fun RhythmBpmItem(
    bpm: Int,
    isSelected: Boolean,
    onBpmSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) White else Gray100)
            .border(
                width = 2.dp,
                color = if (isSelected) Purple50 else Gray300,
                shape = RoundedCornerShape(25.dp)
            )
            .clickableWithoutRipple { onBpmSelected(bpm) }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
       FixedText(
            text = bpm.toString(),
            color = if (isSelected) Purple50 else Gray500,
            style = StempoTheme.typography.head3
        )
    }
}