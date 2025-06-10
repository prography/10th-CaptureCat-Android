package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.prography.home.ui.home.HomeScreen
import com.prography.home.ScreenshotGalleryScreen
import com.prography.home.ui.DummyScreen

@Composable
fun MainNavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Storage.route) {
            DummyScreen(onLogout = { /* ... */ })
        }
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                onNavigateToStorage = {
                    navController.navigate(BottomNavItem.Storage.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(BottomNavItem.Search.route) {
            DummyScreen(onLogout = { /* ... */ })
        }
        composable("screenshot_gallery") {
            ScreenshotGalleryScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}
