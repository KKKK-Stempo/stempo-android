package com.kkkk.presentation.main.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.result.ResultRoute

fun NavController.navigateToResult(
    navOptions: NavOptions? = null
) {
    navigate(MainTabRoute.Result, navOptions)
}

fun NavGraphBuilder.resultNavGraph(
) {
    composable<MainTabRoute.Result> {
        ResultRoute()
    }
}
