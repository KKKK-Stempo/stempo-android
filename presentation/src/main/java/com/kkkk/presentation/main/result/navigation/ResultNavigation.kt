package com.kkkk.presentation.main.result.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.result.ResultRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToResult() {
    navigate(Result)
}

fun NavGraphBuilder.resultNavGraph(
) {
    composable<Result> {
        ResultRoute()
    }
}

@Serializable
private data object Result
