package com.kkkk.presentation.main.rhythm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.rhythm.component.RhythmModeToggle
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
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
    onChangeBtnClick: () -> Unit = {}
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

        Text(
            modifier = Modifier
                .padding(top = 26.dp, start = 60.dp, end = 60.dp)
                .align(Alignment.CenterHorizontally),
            text = if (selectedMode == RhythmMode.RHYTHM) {
                stringResource(R.string.rhythm_tv_title)
            } else {
                stringResource(R.string.stretch_tv_title)
            },
            style = StempoTheme.typography.head1
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Dark)
                .clickable { onChangeBtnClick() }
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_change),
                contentDescription = null,
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = stringResource(id = R.string.rhythm_btn_change_level),
                style = StempoTheme.typography.head3,
                color = White
            )
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