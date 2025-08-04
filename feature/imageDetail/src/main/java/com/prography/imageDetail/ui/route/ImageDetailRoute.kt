package com.prography.imageDetail.ui.route

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.prography.imageDetail.ui.screen.ImageDetailScreen
import com.prography.imageDetail.ui.viewmodel.ImageDetailViewModel
import com.prography.navigation.NavigationHelper
import com.prography.navigation.NavigationEvent

@Composable
fun ImageDetailRoute(
    navigationHelper: NavigationHelper,
    navController: NavHostController,
    screenshotIds: List<String>,
    currentIndex: Int,
    entryPoint: String
) {
    val viewModel: ImageDetailViewModel = hiltViewModel()

    ImageDetailScreen(
        screenshotIds = screenshotIds,
        currentIndex = currentIndex,
        onNavigateBack = {
            navigationHelper.navigate(NavigationEvent.Up)
        },
        entryPoint = entryPoint,
        viewModel = viewModel
    )
}