package com.prography.auth.route.navigation

import androidx.compose.runtime.Composable
import com.prography.auth.route.screen.ui.LoginScreen
import com.prography.navigation.NavigationHelper

@Composable
fun LoginRoute(navigationHelper: NavigationHelper) {
    LoginScreen(navigationHelper = navigationHelper)
}