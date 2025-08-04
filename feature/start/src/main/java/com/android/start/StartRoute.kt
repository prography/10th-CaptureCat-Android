package com.android.start

import androidx.compose.runtime.*
import com.prography.navigation.NavigationHelper
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.util.MixpanelUtil
import com.prography.util.permission.ScreenshotPermissionGate

@Composable
fun StartRoute(navigationHelper: NavigationHelper) {
    // StartRoute는 바로 StartTag로 리다이렉트
    LaunchedEffect(Unit) {
        navigationHelper.navigate(NavigationEvent.To(AppRoute.StartTag))
    }
}

// Screen 상태를 정의하는 Enum
enum class ScreenState {
    TAG_SCREEN,
    CHOOSE_SCREEN
}
