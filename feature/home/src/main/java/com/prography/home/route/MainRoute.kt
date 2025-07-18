package com.prography.home.route

import androidx.compose.runtime.Composable
import com.prography.home.ui.storage.MainScreen
import com.prography.navigation.NavigationHelper

@Composable
fun MainRoute(navigationHelper: NavigationHelper) {
    MainScreen(navigationHelper)
}