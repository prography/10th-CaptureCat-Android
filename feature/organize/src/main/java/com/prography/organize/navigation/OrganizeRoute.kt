package com.prography.organize.navigation

import androidx.compose.runtime.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.navigation.OrganizeDataCache
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.OrganizeScreen

@Composable
fun OrganizeRoute(
    navigationHelper: NavigationHelper,
    screenshots: List<OrganizeScreenshotItem> = emptyList()
) {
    // OrganizeDataCache에서 실제 데이터 가져오기
    val cachedData = remember { OrganizeDataCache.getScreenshots() }

    val actualScreenshots = if (cachedData.isNotEmpty()) {
        cachedData.map { data ->
            OrganizeScreenshotItem(
                id = data.id,
                uri = data.uri,
                fileName = data.fileName
            )
        }
    } else if (screenshots.isNotEmpty()) {
        screenshots
    } else {
        // 더미 데이터 (테스트용)
        listOf(
            OrganizeScreenshotItem(
                id = "1",
                uri = android.net.Uri.EMPTY,
                fileName = "screenshot_1.png"
            ),
            OrganizeScreenshotItem(
                id = "2",
                uri = android.net.Uri.EMPTY,
                fileName = "screenshot_2.png"
            )
        )
    }

    // 캐시 정리 (메모리 절약)
    LaunchedEffect(Unit) {
        if (cachedData.isNotEmpty()) {
            OrganizeDataCache.clear()
        }
    }

    OrganizeScreen(
        screenshots = actualScreenshots,
        currentIndex = 0,
        onNavigateUp = {
            navigationHelper.navigate(NavigationEvent.Up)
        },
        onComplete = {
            // 완료 상태 설정
            OrganizeDataCache.setCompleted()
            // 메인 화면으로 돌아가기 (전체 스택 정리)
            navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
        }
    )
}
