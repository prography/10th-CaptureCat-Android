package com.prography.util.permission

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import android.Manifest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.prography.ui.R
import com.prography.ui.component.UiBasicDialog
import timber.log.Timber


enum class PermissionStatus {
    GRANTED, SHOULD_SHOW_RATIONALE, DENIED_FOREVER
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScreenshotPermissionGate(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionJustGranted: (() -> Unit)? = null,
    onNavigateToSettings: () -> Unit = {}
) {
    val permission = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_IMAGES
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)
    var alreadyVisited by rememberSaveable { mutableStateOf(false) }

    val status = when {
        permissionState.status.isGranted -> PermissionStatus.GRANTED
        !permissionState.status.shouldShowRationale -> PermissionStatus.SHOULD_SHOW_RATIONALE
        else -> PermissionStatus.DENIED_FOREVER
    }

    // 시스템 alert 최초 1회만 실행 & 거부안한 경우에만
    LaunchedEffect(Unit) {
        if (!alreadyVisited && status == PermissionStatus.SHOULD_SHOW_RATIONALE) {
            alreadyVisited = true
            permissionState.launchPermissionRequest()
            return@LaunchedEffect
        }
    }



    when (status) {
        PermissionStatus.GRANTED -> {
            Timber.d("권한 허용")
            onPermissionJustGranted?.invoke()
            onPermissionGranted()
        }

        PermissionStatus.SHOULD_SHOW_RATIONALE -> {
            Timber.d("처음 권한 요청")
        }

        PermissionStatus.DENIED_FOREVER -> {
            Timber.d("권한 거절")
            UiBasicDialog(
                isVisible = true,
                title = stringResource(R.string.permission_denied_title),
                info = stringResource(R.string.permission_setting_info),
                confirmButtonText = stringResource(R.string.permission_setting),
                onConfirm = onNavigateToSettings
            )
        }
    }
}
