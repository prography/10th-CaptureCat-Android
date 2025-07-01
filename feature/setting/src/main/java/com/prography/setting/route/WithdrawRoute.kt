package com.prography.setting.route

import androidx.compose.runtime.Composable
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.setting.ui.SettingsScreen
import com.prography.setting.ui.WithdrawScreen

@Composable
fun WithdrawRoute(
    navigationHelper: NavigationHelper
) {
    WithdrawScreen(
        onNavigateBack = {
            navigationHelper.navigate(NavigationEvent.Up)
        },
        onWithdrawComplete = {
            navigationHelper.navigate(
                NavigationEvent.To(AppRoute.Login, popUpTo = true)
            )
        }
    )
}