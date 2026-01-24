package com.prography.home.bottomNav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.prography.favorite.ui.route.FavoriteRoute
import com.prography.home.ui.home.HomeScreen
import com.prography.home.ui.search.screen.SearchScreen

@Composable
fun MainNavigationHost(
    screenshotIds: List<String>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var initialScreenshotIds by remember { mutableStateOf(screenshotIds) }

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
                screenshotIds = initialScreenshotIds,
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
            LaunchedEffect(Unit) {
                if (initialScreenshotIds.isNotEmpty()) {
                    // 약간의 딜레이 후 초기화 (HomeScreen이 처리할 시간 주기)
                    kotlinx.coroutines.delay(100)
                    initialScreenshotIds = emptyList()
                }
            }
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