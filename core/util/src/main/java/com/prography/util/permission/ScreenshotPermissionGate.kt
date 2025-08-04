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
import androidx.compose.runtime.setValue
import com.prography.ui.component.UiBasicDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScreenshotPermissionGate(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionJustGranted: (() -> Unit)? = null,
    onPermissionDenied: (() -> Unit)? = null,
    onNavigateToSettings: () -> Unit = {}
) {
    val permission = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_IMAGES
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Manifest.permission.READ_EXTERNAL_STORAGE
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)
    var showRationaleDialog by remember { mutableStateOf(true) }

    // 권한이 허용되었을 때 콜백 호출 (단순하게)
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            onPermissionJustGranted?.invoke()
        }
    }

    when {
        permissionState.status.isGranted -> {
            onPermissionGranted()
        }

        permissionState.status.shouldShowRationale -> {
            UiBasicDialog(
                isVisible = showRationaleDialog,
                title = "사진 접근 불가!",
                info = "스크린샷을 불러오기 위해 사진 접근 권한이 필요합니다.",
                confirmButtonText = "권한 요청",
                onConfirm = {
                    showRationaleDialog = false
                    permissionState.launchPermissionRequest()
                }
            )
        }

        else -> {
            // permission denied (첫 요청 또는 영구 거부)

            // 최초 진입이면 자동 요청 시도
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }

            // 영구 거부 상태 처리
            if (!permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
                if (onPermissionDenied != null) {
                    onPermissionDenied()
                } else {
                    UiBasicDialog(
                        isVisible = true,
                        title = "사진 접근 불가!",
                        info = "설정에서 사진 접근 권한을 허용해주세요.",
                        confirmButtonText = "설정으로",
                        onConfirm = onNavigateToSettings
                    )
                }
            }
        }
    }
}
