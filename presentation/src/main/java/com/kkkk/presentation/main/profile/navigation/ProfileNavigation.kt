package com.kkkk.presentation.main.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.profile.ProfileRoute

fun NavController.navigateToProfile(
    navOptions: NavOptions? = null
) {
    navigate(MainTabRoute.Profile, navOptions)
}

fun NavGraphBuilder.profileNavGraph(
) {
    composable<MainTabRoute.Profile> {
        ProfileRoute()
    }
}
