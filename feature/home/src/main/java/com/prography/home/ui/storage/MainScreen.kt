package com.prography.home.ui.storage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prography.home.bottomNav.BottomNavItem
import com.prography.home.bottomNav.BottomNavigationBar
import com.prography.home.bottomNav.MainNavigationHost
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.screen.ScreenshotBottomBar
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.navigation.NavigationHelper

@Composable
fun MainScreen(navigationHelper: NavigationHelper) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenshotViewModel: ScreenshotViewModel = hiltViewModel()
    val screenshotState by screenshotViewModel.uiState.collectAsState()

    val showScreenshotBottomBar =
        currentRoute == BottomNavItem.Storage.route && screenshotState.isSelectionMode

    // 탭이 변경될 때 선택 상태 초기화
    LaunchedEffect(currentRoute) {
        if (currentRoute != BottomNavItem.Storage.route && screenshotState.isSelectionMode) {
            screenshotViewModel.sendAction(ScreenshotAction.CancelSelection)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showScreenshotBottomBar) {
                ScreenshotBottomBar(
                    selectedCount = screenshotState.selectedCount,
                    onAction = { screenshotViewModel.sendAction(it) }
                )
            } else {
                BottomNavigationBar(
                    navController = navController,
                    onItemSelected = { /* 탭 클릭 시 */ }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            MainNavigationHost(
                navController = navController,
                screenshotViewModel = screenshotViewModel
            )
        }
    }
}
