package com.kkkk.presentation.main.homework.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.homework.HomeworkRoute

fun NavController.navigateToHomework(
    navOptions: NavOptions? = null
) {
    navigate(MainTabRoute.Homework, navOptions)
}

fun NavGraphBuilder.homeworkNavGraph(
) {
    composable<MainTabRoute.Homework> {
        HomeworkRoute()
    }
}
