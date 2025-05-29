package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prography.home.MainContent
import com.prography.home.ScreenshotGalleryScreen
import com.prography.home.screen.HomeScreen
import com.prography.home.screen.RandomPhotoScreen

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

        // 기타 서브 화면도 여기에
        composable("screenshot_gallery") {
            ScreenshotGalleryScreen(onNavigateUp = { navController.navigateUp() })
        }
    }
}