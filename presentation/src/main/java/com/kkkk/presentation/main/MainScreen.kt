package com.kkkk.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.kkkk.presentation.main.profile.navigation.profileNavGraph
import com.kkkk.presentation.main.record.navigation.recordNavGraph
import com.kkkk.presentation.main.homework.navigation.homeworkNavGraph
import com.kkkk.presentation.main.rhythm.navigation.rhythmNavGraph
import com.kkkk.presentation.main.theme.White
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    Scaffold(
        bottomBar = {
            MainBottomBar(
                visible = navigator.shouldShowBottomBar(),
                tabs = BottomTabItem.entries.toImmutableList(),
                currentTab = navigator.currentTab,
                onTabSelected = { tab ->
                    navigator.navigate(tab)
                }
            )
        },
        content = { paddingValue ->
            NavHost(
                modifier = Modifier
                    .padding(paddingValue)
                    .background(White),
                startDestination = navigator.startDestination,
                navController = navigator.navController,
            ) {
                recordNavGraph()
                rhythmNavGraph()
                homeworkNavGraph()
                profileNavGraph()
            }
        }
    )
}
