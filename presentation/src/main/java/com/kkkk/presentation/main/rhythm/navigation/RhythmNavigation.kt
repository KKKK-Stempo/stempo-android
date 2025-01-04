package com.kkkk.presentation.main.rhythm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.rhythm.RhythmRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToRhythm() {
    navigate(Rhythm)
}

fun NavGraphBuilder.rhythmNavGraph(
) {
    composable<Rhythm> {
        RhythmRoute()
    }
}

@Serializable
private data object Rhythm
