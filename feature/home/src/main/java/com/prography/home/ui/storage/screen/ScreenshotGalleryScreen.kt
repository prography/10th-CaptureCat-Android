package com.prography.home.ui.storage.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

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
        },
        onNavigateToSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    )
}