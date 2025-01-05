package com.kkkk.presentation.main.rhythm

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.rhythm.component.RhythmModeToggle
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.stempo.presentation.R

@Composable
fun RhythmRoute() {
    var selectedMode by remember { mutableStateOf(RhythmMode.RHYTHM) }

    RhythmScreen(
        selectedMode = selectedMode,
        onToggleSelected = { selectedMode = it }
    )
}

@Composable
internal fun RhythmScreen(
    selectedMode: RhythmMode,
    onToggleSelected: (RhythmMode) -> Unit = {},
    onWatchBtnClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.padding(top = 24.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            RhythmModeToggle(
                modifier = Modifier.padding(horizontal = 60.dp),
                selectedMode = selectedMode,
                onToggleSelected = onToggleSelected
            )
            if (selectedMode == RhythmMode.RHYTHM) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_watch),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onWatchBtnClick() }
                        .padding(6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RhythmScreenPreview() {
    StempoTheme {
        RhythmScreen(selectedMode = RhythmMode.RHYTHM)
    }
}