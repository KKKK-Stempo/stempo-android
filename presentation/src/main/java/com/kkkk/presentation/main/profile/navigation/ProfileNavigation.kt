package com.kkkk.presentation.main.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kkkk.presentation.main.profile.ProfileRoute
import kotlinx.serialization.Serializable

fun NavController.navigateToProfile() {
    navigate(Profile)
}

fun NavGraphBuilder.profileNavGraph(
) {
    composable<Profile> {
        ProfileRoute()
    }
}

@Serializable
private data object Profile
