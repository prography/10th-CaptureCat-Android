package com.prography.home.route

import androidx.compose.runtime.Composable
import com.prography.home.ui.storage.MainScreen

@Composable
fun MainRoute(
    screenshotIds: List<String> = emptyList()
) {
    MainScreen(screenshotIds)
}