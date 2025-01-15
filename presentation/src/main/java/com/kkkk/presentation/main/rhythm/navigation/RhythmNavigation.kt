package com.kkkk.presentation.main.rhythm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.rhythm.RhythmRoute

fun NavController.navigateToRhythm(
    navOptions: NavOptions? = null,
) {
    navigate(MainTabRoute.Rhythm, navOptions)
}

fun NavGraphBuilder.rhythmNavGraph(
) {
    composable<MainTabRoute.Rhythm> {
        RhythmRoute()
    }
}
