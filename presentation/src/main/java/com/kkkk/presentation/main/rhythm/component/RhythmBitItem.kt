package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.theme.Gray200
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White

@Composable
fun RhythmBitItem(
    bit: Int,
    isSelected: Boolean,
    onBitClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(if (isSelected) Purple50 else Gray200)
            .clickableWithoutRipple { onBitClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$bit 박자",
            style = StempoTheme.typography.head2,
            color = if (isSelected) White else Gray600
        )
    }
}