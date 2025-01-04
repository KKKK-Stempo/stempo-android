package com.kkkk.presentation.main.rhythm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.rhythm.RhythmRoute

fun NavController.navigateToRhythm() {
    navigate(MainTabRoute.Rhythm)
}

fun NavGraphBuilder.rhythmNavGraph(
) {
    composable<MainTabRoute.Rhythm> {
        RhythmRoute()
    }
}
