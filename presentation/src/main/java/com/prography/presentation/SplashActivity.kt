package com.prography.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.prography.ui.R
import com.prography.ui.theme.PrographyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber
import com.prography.ui.component.UiBasicDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 배경이 항상 흰색이므로 상태바 아이콘을 검정색으로 설정
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.WHITE,
                darkScrim = android.graphics.Color.WHITE
            )
        )

        setContent {
            PrographyTheme {
                SplashScreen(
                    onNavigateToMain = {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        finish()
                    },
                    onForceUpdate = {
                        // 플레이스토어로 이동 (마켓 앱 우선, 실패 시 웹)
                        val appPackage = packageName
                        val marketUri = "market://details?id=$appPackage".toUri()
                        val webUri =
                            "https://play.google.com/store/apps/details?id=$appPackage".toUri()
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, marketUri))
                        } catch (_: Exception) {
                            startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun SplashScreen(
        onNavigateToMain: () -> Unit,
        onForceUpdate: () -> Unit
    ) {

        val alpha = remember {
            Animatable(0f)
        }
        val ctx = LocalContext.current
        var showUpdateDialog by remember { mutableStateOf(false) }
        var showMaintenanceDialog by remember { mutableStateOf(false) }
        var maintenanceMessage by remember { mutableStateOf("") }

        LaunchedEffect(key1 = Unit) {
            // Remote Config 초기화 및 fetch
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(settings)
            // 기본값(없을 때)을 안전하게 지정
            remoteConfig.setDefaultsAsync(
                mapOf(
                    "android_min_supported_version" to "1.0.0",
                    "maintenance_end_time" to "2030-01-01T00:00:00+09:00", // 과거 시간
                    "android_force_update" to false
                )
            )

            runCatching {
                remoteConfig.fetchAndActivate().await()
            }.onFailure { /* 무시하고 정상 흐름 진행 */ }

            // 1. 서버 점검 시간 여부 확인
            val maintenanceStartTimeStr = remoteConfig.getString("maintenance_start_time")
            val maintenanceEndTimeStr = remoteConfig.getString("maintenance_end_time")

            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA)
            val displayFormat = SimpleDateFormat("MM.dd(E) H:mm", Locale.KOREA)

            val maintenanceStart = format.parse(maintenanceStartTimeStr)
            val maintenanceEnd = format.parse(maintenanceEndTimeStr)

            val now = Date()

            if (now.after(maintenanceStart) && now.before(maintenanceEnd)) {
                val startStr = maintenanceStart?.let { displayFormat.format(it) } ?: ""
                val endStr = maintenanceEnd?.let { displayFormat.format(it) } ?: ""

                maintenanceMessage = """
        $startStr ~  $endStr
        
        보다 안정적인 서비스 제공을 위해 서비스 개편이 진행될 예정입니다. 캡처캣을 이용해주셔서 감사합니다.
    """.trimIndent()

                showMaintenanceDialog = true
                return@LaunchedEffect
            }


            val forceUpdate = remoteConfig.getBoolean("Android_force_update")

            // 2. 인앱 업데이트 가능 여부 확인 & 디버그 모드 아닐때만 확인
            if (!BuildConfig.DEBUG && forceUpdate) {
                val appUpdateManager = AppUpdateManagerFactory.create(ctx)
                val updateInfo = appUpdateManager.appUpdateInfo.await()

                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    showUpdateDialog = true
                    return@LaunchedEffect
                }
            } else {
                // 3. 최소 버전 업데이트 확인
                val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
                val currentVersion = pInfo.versionName ?: "0.0.0"
                val minSupported = remoteConfig.getString("Android_min_supported_version")
                if (compareVersionNames(currentVersion, minSupported)) {
                    showUpdateDialog = true
                    return@LaunchedEffect
                }
            }

            // 업데이트 필요 없으면 기존 애니메이션 후 메인으로 이동
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(500)
            )
            delay(1000L)
            onNavigateToMain()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_splash_logo),
                contentDescription = stringResource(R.string.cd_capture_cat_logo),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (LocalConfiguration.current.screenHeightDp * 0.4f).dp)
                    .alpha(alpha.value),
            )
        }

        if (showUpdateDialog) {
            UiBasicDialog(
                isVisible = true,
                title = stringResource(R.string.app_update_title),
                info = stringResource(R.string.app_update_info),
                confirmButtonText = stringResource(R.string.app_update_button),
                onConfirm = onForceUpdate
            )
        }

        if (showMaintenanceDialog) {
            UiBasicDialog(
                isVisible = true,
                title = stringResource(R.string.maintenance_title),
                info = maintenanceMessage,
                confirmButtonText = "",
                onConfirm = { finish() } // 앱 종료
            )
        }
    }

    private fun compareVersionNames(current: String, minimum: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val minimumParts = minimum.split(".").map { it.toIntOrNull() ?: 0 }

        val size = maxOf(currentParts.size, minimumParts.size)
        for (i in 0 until size) {
            val currentPart = currentParts.getOrElse(i) { 0 }
            val minimumPart = minimumParts.getOrElse(i) { 0 }
            if (currentPart < minimumPart) return true
            if (currentPart > minimumPart) return false
        }
        return false
    }

    @Preview(showBackground = true)
    @Composable
    fun SplashScreenPreview() {
        PrographyTheme {
            SplashScreen(
                onNavigateToMain = {},
                onForceUpdate = {}
            )
        }
    }
}