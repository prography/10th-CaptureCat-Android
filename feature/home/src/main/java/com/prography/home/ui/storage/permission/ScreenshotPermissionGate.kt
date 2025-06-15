package com.prography.home.ui.storage.permission

import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScreenshotPermissionGate(
    onPermissionGranted: @Composable () -> Unit
) {
    val permission = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> android.Manifest.permission.READ_MEDIA_IMAGES
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> android.Manifest.permission.READ_EXTERNAL_STORAGE
        else -> android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)

    when {
        permissionState.status.isGranted -> {
            onPermissionGranted()
        }

        permissionState.status.shouldShowRationale -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("스크린샷을 불러오려면 권한이 필요합니다.")
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("권한 요청")
                }
            }
        }

        else -> {
            // 최초 진입일 때는 자동 요청
            SideEffect {
                permissionState.launchPermissionRequest()
            }
            // UI 보여줄 필요 없을 땐 빈 화면 or 로딩 처리
        }
    }
}
