package com.kkkk.presentation.main.rhythm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RhythmRoute() {
    RhythmScreen()
}

@Composable
internal fun RhythmScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun RhythmScreenPreview() {
    RhythmScreen()
}