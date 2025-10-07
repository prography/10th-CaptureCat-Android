package com.prography.home.ui.storage.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.util.permission.ScreenshotPermissionGate
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import com.prography.navigation.StorageMode

@Composable
fun SelectPictureScreen(
    mode: StorageMode
) {

    val viewModel: ScreenshotViewModel = hiltViewModel()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) {
        viewModel.refreshScreenshots()
    }

    ScreenshotPermissionGate(
        onPermissionGranted = {
            ScreenshotOrganizeScreen(viewModel = viewModel, mode = mode)
        },
        onPermissionJustGranted = {
            viewModel.refreshScreenshots()
        },
        onNavigateToSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    )
}