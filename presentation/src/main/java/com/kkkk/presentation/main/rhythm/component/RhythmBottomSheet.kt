package com.kkkk.presentation.main.rhythm.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.rhythm.RhythmState
import com.kkkk.presentation.main.theme.Gray200
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhythmBottomSheet(
    rhythmState: RhythmState,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onSubmitBtnClick: (tempBit: Int, tempBpm: Int) -> Unit = { _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState()

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
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "박자 선택",
                style = StempoTheme.typography.head4,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 20.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                items(listOf(2, 3, 4, 6, 8)) { bit ->
                    RhythmBitItem(
                        bit = bit,
                        isSelected = bit == tempBit,
                        onBitClick = { tempBit = bit }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(2.dp),
                color = Gray300
            )

            Text(
                text = "빠르기 선택",
                style = StempoTheme.typography.head4,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun RhythmBottomSheetPreview() {
    StempoTheme {
        RhythmBottomSheet(
            rhythmState = RhythmState()
        )
    }
}