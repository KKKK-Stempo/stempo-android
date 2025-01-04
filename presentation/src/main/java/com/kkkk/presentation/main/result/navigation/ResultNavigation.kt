package com.kkkk.presentation.main.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.result.ResultRoute

fun NavController.navigateToResult() {
    navigate(MainTabRoute.Result)
}

fun NavGraphBuilder.resultNavGraph(
) {
    composable<MainTabRoute.Result> {
        ResultRoute()
    }
}
