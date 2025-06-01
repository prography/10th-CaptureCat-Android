package com.prography.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.prography.home.bottomNav.BottomNavigationBar
import com.prography.home.bottomNav.MainNavigationHost
import com.prography.navigation.NavigationHelper

@Composable
fun MainScreen(navigationHelper: NavigationHelper) {

    val navController = rememberNavController()


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                onItemSelected = { /* 탭 클릭 시 */ }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            MainNavigationHost(navController = navController)
        }
    }
}
