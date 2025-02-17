package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.component.FixedText
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.rhythm.RhythmState
import com.kkkk.presentation.main.rhythm.RhythmState.Companion.MAX_BPM
import com.kkkk.presentation.main.rhythm.RhythmState.Companion.MIN_BPM
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.Purple10
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R
import okhttp3.internal.immutableListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhythmBottomSheet(
    rhythmState: RhythmState,
    sheetState: SheetState = rememberModalBottomSheetState(),
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onSubmitBtnClick: (tempBit: Int, tempBpm: Int) -> Unit = { _, _ -> }
) {
    var tempBit by remember { mutableIntStateOf(rhythmState.bit) }
    var tempBpm by remember { mutableIntStateOf(rhythmState.bpm) }

    ModalBottomSheet(
        modifier = modifier.fillMaxSize(),
        containerColor = White,
        onDismissRequest = {
            onDismissRequest()
        },
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_drag_handle),
                    contentDescription = null,
                    tint = Gray500,
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        ) {
           FixedText(
                text = "박자 선택",
                style = StempoTheme.typography.head4,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            RhythmBitSelectGrid(
                tempBit = tempBit,
                onBitClick = { tempBit = it }
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(2.dp),
                color = Gray300
            )

          FixedText(
                text = "빠르기 선택",
                style = StempoTheme.typography.head4,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

            RhythmBpmUpDownBtns(
                tempBpm = tempBpm,
                onMinusBtnClick = { if (tempBpm > MIN_BPM) tempBpm -= 5 },
                onPlusBtnClick = { if (tempBpm < MAX_BPM) tempBpm += 5 }
            )

            RhythmBpmSelectGrid(
                tempBpm = tempBpm,
                onBpmSelected = { tempBpm = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            RhythmSubmitBtn(
                onSubmitBtnClick = { onSubmitBtnClick(tempBit, tempBpm) }
            )
        }
    }
}

@Composable
fun RhythmBitSelectGrid(
    tempBit: Int,
    onBitClick: (bit: Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        items(immutableListOf(2, 3, 4, 6, 8), key = { it }) { bit ->
            RhythmBitItem(
                bit = bit,
                isSelected = bit == tempBit,
                onBitClick = { onBitClick(bit) }
            )
        }
    }
}

@Composable
fun RhythmBpmUpDownBtns(
    tempBpm: Int,
    onMinusBtnClick: () -> Unit,
    onPlusBtnClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RhythmUpDownBtn(
            iconResource = if (tempBpm > MIN_BPM) R.drawable.ic_minus_purple else R.drawable.ic_minus_gray,
            isEnabled = tempBpm > MIN_BPM,
            onClick = { if (tempBpm > MIN_BPM) onMinusBtnClick() },
            modifier = Modifier.padding(start = 20.dp)
        )
        FixedText(
            text = "$tempBpm",
            style = StempoTheme.typography.head1,
        )
        RhythmUpDownBtn(
            iconResource = if (tempBpm < MAX_BPM) R.drawable.ic_plus_purple else R.drawable.ic_plus_gray,
            isEnabled = tempBpm < MAX_BPM,
            onClick = { if (tempBpm < MAX_BPM) onPlusBtnClick() },
            modifier = Modifier.padding(end = 20.dp)
        )
    }
}

@Composable
fun RhythmBpmSelectGrid(
    tempBpm: Int,
    onBpmSelected: (bpm: Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items(immutableListOf(65, 75, 85, 95, 105, 115), key = { it }) { bpm ->
            RhythmBpmItem(
                bpm = bpm,
                isSelected = tempBpm == bpm,
                onBpmSelected = { onBpmSelected(bpm) }
            )
        }
    }
}

@Composable
fun ColumnScope.RhythmSubmitBtn(
    onSubmitBtnClick: () -> Unit
) {
    FixedText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
            .align(Alignment.CenterHorizontally)
            .background(
                shape = RoundedCornerShape(12.dp),
                color = Purple10
            )
            .clickableWithoutRipple { onSubmitBtnClick() }
            .padding(vertical = 15.dp),
        text = stringResource(R.string.rhythm_btn_submit_level),
        textAlign = TextAlign.Center,
        color = Purple50,
        style = StempoTheme.typography.head4
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RhythmBottomSheetPreview() {
    StempoTheme {
        val sheetState: SheetState = rememberModalBottomSheetState()
        RhythmBottomSheet(
            rhythmState = RhythmState(),
            sheetState = sheetState
        )
    }
}