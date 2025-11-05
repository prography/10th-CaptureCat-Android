package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.prography.favorite.ui.route.FavoriteRoute
import com.prography.home.ui.home.HomeScreen
import com.prography.home.ui.search.screen.SearchScreen
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel

@Composable
fun MainNavigationHost(
    screenshotIds: List<String>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Favorite.route) {
            FavoriteRoute()
        }
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                screenshotIds = screenshotIds,
                onNavigateToStorage = {
                    navController.navigate(BottomNavItem.Favorite.route) {
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
                    navController.navigate(BottomNavItem.Favorite.route) {
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