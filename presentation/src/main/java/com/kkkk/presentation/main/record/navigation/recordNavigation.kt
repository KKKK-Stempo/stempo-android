package com.kkkk.presentation.main.record.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.record.RecordRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToRecord() {
    navigate(Record)
}

fun NavGraphBuilder.recordNavGraph(
) {
    composable<Record> {
        RecordRoute()
    }
}

@Serializable
private data object Record
