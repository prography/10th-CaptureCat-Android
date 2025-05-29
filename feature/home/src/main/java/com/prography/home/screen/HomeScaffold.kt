package com.prography.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prography.home.MainContent
import com.prography.home.ScreenshotGalleryScreen
import com.prography.home.ToDoViewModel
import com.prography.home.bottomNav.BottomNavigationBar
import com.prography.home.bottomNav.MainNavigationHost

@Composable
fun HomeScaffold(
) {
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
