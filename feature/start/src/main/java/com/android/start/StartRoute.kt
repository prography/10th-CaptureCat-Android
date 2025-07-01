package com.android.start

import androidx.compose.runtime.*
import com.prography.navigation.NavigationHelper
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.util.permission.ScreenshotPermissionGate

@Composable
fun StartRoute(navigationHelper: NavigationHelper) {
    var currentScreen by remember { mutableStateOf(ScreenState.TAG_SCREEN) }

    when (currentScreen) {
        ScreenState.TAG_SCREEN -> {
            // 처음 보여지는 태그 선택 화면
            StartTagScreen(
                onFinishSelection = {
                    currentScreen = ScreenState.CHOOSE_SCREEN // 화면 전환
                }
            )
        }
        ScreenState.CHOOSE_SCREEN -> {
            // 선택 완료 후 캡처 스크린샷 관리 화면
            ScreenshotPermissionGate(
                onPermissionGranted = {
                    StartChooseScreen(
                        maxSelectableImages = 10,
                        onFinishSelection = { selectedImages ->
                            navigationHelper.navigate(
                                NavigationEvent.To(AppRoute.Organize(screenshotIds = selectedImages.map { it.id }))
                            )
                        }
                    )
                }
            )
        }
    }
}

// Screen 상태를 정의하는 Enum
enum class ScreenState {
    TAG_SCREEN,
    CHOOSE_SCREEN
}
