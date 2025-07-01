package com.prography.home.ui.storage.screen

import androidx.compose.runtime.*
import com.prography.util.permission.ScreenshotPermissionGate
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel

@Composable
fun ScreenshotGalleryScreen(
    onNavigateUp: () -> Unit,
    screenshotViewModel: ScreenshotViewModel
) {
    ScreenshotPermissionGate(
        onPermissionGranted = {
            ScreenshotOrganizeScreen(viewModel = screenshotViewModel)
        },
        onPermissionJustGranted = {
            screenshotViewModel.refreshScreenshots()
        }
    )
}