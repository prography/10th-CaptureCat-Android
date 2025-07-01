package com.prography.setting.route

import androidx.compose.runtime.Composable
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.setting.ui.SettingsScreen

@Composable
fun SettingRoute(navigationHelper: NavigationHelper) {
    SettingsScreen(
        onNavigateUp = {
            navigationHelper.navigate(NavigationEvent.Up)
        },
        onNavigateToLogin = {
            navigationHelper.navigate(
                NavigationEvent.To(AppRoute.Login, popUpTo = true)
            )
        },
        onNavigateToWithdraw = {
            navigationHelper.navigate(
                NavigationEvent.To(AppRoute.SettingRoute.Withdraw, popUpTo = true)
            )
        }
    )
}