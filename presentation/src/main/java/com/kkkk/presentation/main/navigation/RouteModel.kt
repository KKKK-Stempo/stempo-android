package com.kkkk.presentation.main.navigation

import kotlinx.serialization.Serializable

// BottomNavigation 없어도 되는 화면
sealed interface Route {
}

// BottomNavigation 있어야 하는 화면
sealed interface MainTabRoute : Route {
    @Serializable
    data object Rhythm : MainTabRoute

    @Serializable
    data object Record : MainTabRoute

    @Serializable
    data object Result : MainTabRoute

    @Serializable
    data object Profile : MainTabRoute
}
