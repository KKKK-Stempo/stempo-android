package com.kkkk.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.profile.navigation.navigateToProfile
import com.kkkk.presentation.main.record.navigation.navigateToRecord
import com.kkkk.presentation.main.result.navigation.navigateToResult
import com.kkkk.presentation.main.rhythm.navigation.navigateToRhythm

class MainNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val startDestination = MainTabRoute.Rhythm

    val currentTab: BottomTabItem?
        @Composable get() = BottomTabItem.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    @Composable
    fun shouldShowBottomBar() = BottomTabItem.contains {
        currentDestination?.hasRoute(it::class) == true
    }

    fun navigate(tab: BottomTabItem) {
        val navOptions = navOptions {
            navController.currentDestination?.route?.let {
                popUpTo(it) {
                    inclusive = true
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            BottomTabItem.RECORD -> navController.navigateToRecord(navOptions)
            BottomTabItem.RHYTHM -> navController.navigateToRhythm(navOptions)
            BottomTabItem.RESULT -> navController.navigateToResult(navOptions)
            BottomTabItem.PROFILE -> navController.navigateToProfile(navOptions)
        }
    }
}


@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
