package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.prography.home.ui.home.MainContent
import com.prography.home.ScreenshotGalleryScreen
import com.prography.home.ui.HomeScreen

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
        composable(BottomNavItem.Home.route) {
            MainContent(
                onNavigateToScreenshotGallery = {
                    navController.navigate("screenshot_gallery")
                }
            )
        }
        composable(BottomNavItem.Folder.route) {
            HomeScreen(onLogout = { /* ... */ })
        }
        composable(BottomNavItem.Search.route) {
            HomeScreen(onLogout = { /* ... */ })
        }
        composable(BottomNavItem.Storage.route) {
            HomeScreen(onLogout = { /* ... */ })
        }

        composable("screenshot_gallery") {
            ScreenshotGalleryScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}