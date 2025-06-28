package com.prography.home.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prography.home.bottomNav.BottomNavItem
import com.prography.home.bottomNav.BottomNavigationBar
import com.prography.home.bottomNav.MainNavigationHost
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.navigation.NavigationHelper
import com.prography.ui.theme.Divider

@Composable
fun MainScreen(navigationHelper: NavigationHelper) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenshotViewModel: ScreenshotViewModel = hiltViewModel()
    val screenshotState by screenshotViewModel.uiState.collectAsState()

    // 탭이 변경될 때 선택 상태 초기화
    LaunchedEffect(currentRoute) {
        if (currentRoute != BottomNavItem.Storage.route && screenshotState.isSelectionMode) {
            screenshotViewModel.sendAction(ScreenshotAction.CancelSelection)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x0D001758))
                )
                BottomNavigationBar(
                    navController = navController,
                    onItemSelected = { /* 탭 클릭 시 */ }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
        ) {
            MainNavigationHost(
                navController = navController,
                screenshotViewModel = screenshotViewModel
            )
        }
    }
}
