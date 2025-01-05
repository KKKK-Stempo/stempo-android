package com.kkkk.presentation.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kkkk.presentation.main.navigation.MainTabRoute
import com.kkkk.presentation.main.navigation.Route
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.Gray300
import com.kkkk.presentation.main.theme.Gray500
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainBottomBar(
    visible: Boolean,
    tabs: ImmutableList<BottomTabItem>,
    currentTab: BottomTabItem?,
    onTabSelected: (BottomTabItem) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) }
    ) {
        Column(
            modifier = Modifier
                .shadow(24.dp)
                .background(White)
        ) {
            HorizontalDivider(
                color = Gray300
            )
            Spacer(modifier = Modifier.height(11.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEach { tab ->
                    MainBottomBarItem(
                        tab = tab,
                        selected = (tab == currentTab),
                        onClick = {
                            onTabSelected(tab)
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun MainBottomBarItem(
    tab: BottomTabItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .selectable(
                selected = selected,
                indication = null,
                role = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(
                if (selected) {
                    tab.selectedIconResource
                } else {
                    tab.unselectedIconResource
                }
            ),
            contentDescription = tab.title,
            tint = Color.Unspecified,
        )

        Text(
            text = tab.title,
            color = if (selected) {
                Dark
            } else {
                Gray500
            },
            style = StempoTheme.typography.caption1
        )
    }
}

enum class BottomTabItem(
    val title: String,
    @DrawableRes val selectedIconResource: Int,
    @DrawableRes val unselectedIconResource: Int,
    val route: MainTabRoute,
) {
    RHYTHM(
        "리듬",
        R.drawable.ic_rhythm_selected,
        R.drawable.ic_rhythm_unselected,
        MainTabRoute.Rhythm
    ),
    RECORD(
        "기록",
        R.drawable.ic_report_selected,
        R.drawable.ic_report_unselected,
        MainTabRoute.Record
    ),
    RESULT(
        "과제",
        R.drawable.ic_study_selected,
        R.drawable.ic_study_unselected,
        MainTabRoute.Result
    ),
    PROFILE(
        "마이",
        R.drawable.ic_my_selected,
        R.drawable.ic_my_unselected,
        MainTabRoute.Profile
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (MainTabRoute) -> Boolean): BottomTabItem? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}
