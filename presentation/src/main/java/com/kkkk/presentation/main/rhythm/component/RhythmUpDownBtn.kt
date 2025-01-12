package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.clickableWithoutRipple

@Composable
fun RhythmUpDownBtn(
    iconResource: Int,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = ImageVector.vectorResource(id = iconResource),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier
            .size(54.dp)
            .clickableWithoutRipple(enabled = isEnabled) { if (isEnabled) onClick() }
    )
}