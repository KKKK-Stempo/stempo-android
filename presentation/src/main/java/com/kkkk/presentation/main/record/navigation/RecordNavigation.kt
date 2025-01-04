package com.kkkk.presentation.main.record.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.record.RecordRoute

fun NavController.navigateToRecord() {
    navigate(MainTabRoute.Record)
}

fun NavGraphBuilder.recordNavGraph(
) {
    composable<MainTabRoute.Record> {
        RecordRoute()
    }
}
