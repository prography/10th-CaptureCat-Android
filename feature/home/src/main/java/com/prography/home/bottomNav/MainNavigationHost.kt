package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.prography.home.ui.home.HomeScreen
import com.prography.home.ui.search.screen.SearchScreen
import com.prography.home.ui.search.screen.SearchResultsScreen
import com.prography.home.ui.storage.screen.ScreenshotGalleryScreen
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel

@Composable
fun MainNavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    screenshotViewModel: ScreenshotViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Storage.route) {
            ScreenshotGalleryScreen(
                onNavigateUp = { navController.navigateUp() },
                screenshotViewModel = screenshotViewModel
            )
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
            SearchScreen(
                onNavigateToStorage = {
                    navController.navigate(BottomNavItem.Storage.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSearchResults = { query ->
                    navController.navigate("search_results/${query}")
                }
            )
        }
        composable("screenshot_gallery") {
            ScreenshotGalleryScreen(
                onNavigateUp = { navController.navigateUp() },
                screenshotViewModel = screenshotViewModel
            )
        }
        composable(
            route = "search_results/{query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(
                query = query,
                onNavigateUp = { navController.navigateUp() },
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
    }
}