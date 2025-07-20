package com.prography.home.ui.storage.screen

import androidx.compose.runtime.*
import com.prography.util.permission.ScreenshotPermissionGate
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun ScreenshotGalleryScreen(
    onNavigateUp: () -> Unit,
    screenshotViewModel: ScreenshotViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh screenshots when screen becomes visible
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) {
        screenshotViewModel.refreshScreenshots()
    }

    ScreenshotPermissionGate(
        onPermissionGranted = {
            ScreenshotOrganizeScreen(viewModel = screenshotViewModel)
        },
        onPermissionJustGranted = {
            screenshotViewModel.refreshScreenshots()
        }
    )
}