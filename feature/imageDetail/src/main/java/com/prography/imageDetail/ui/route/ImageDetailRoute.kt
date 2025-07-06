package com.prography.imageDetail.ui.route

import androidx.compose.runtime.Composable
import com.prography.imageDetail.ui.screen.ImageDetailScreen
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper

@Composable
fun ImageDetailRoute(
    navigationHelper: NavigationHelper,
    screenshotIds: List<String>,
    currentIndex: Int
) {
    ImageDetailScreen(
        screenshotIds = screenshotIds,
        currentIndex = currentIndex,
        onNavigateBack = {
            navigationHelper.navigate(NavigationEvent.Up)
        }
    )
}