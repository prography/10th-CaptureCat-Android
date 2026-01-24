package com.prography.home.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prography.home.bottomNav.BottomNavItem
import com.prography.home.bottomNav.BottomNavigationBar
import com.prography.home.bottomNav.MainNavigationHost
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UiNoticeDialog
import com.prography.ui.theme.Divider

@Composable
fun MainScreen(screenshotIds: List<String>) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenshotViewModel: ScreenshotViewModel = hiltViewModel()
    val screenshotState by screenshotViewModel.uiState.collectAsState()

    // 공지사항 다이얼로그 표시 상태
    var showNoticeDialog by remember { mutableStateOf(true) }

    // 탭이 변경될 때 선택 상태 초기화
    LaunchedEffect(currentRoute) {
        if (currentRoute != BottomNavItem.Favorite.route && screenshotState.isSelectionMode) {
            screenshotViewModel.sendAction(ScreenshotAction.CancelSelection)
        }
    }

    // 공지사항 다이얼로그
    UiNoticeDialog(
        isVisible = showNoticeDialog,
        title = "2026년 1월 28일\n" +
                "캡처캣 서비스가 종료됩니다",
        message = "그동안 캡처캣을 이용해주셔서 감사드리며, \n" +
                "서비스 종료에 관한 상세 내용은 공지사항을 통해 \n" +
                "확인해 주세요.",
        leftButtonText = "공지사항 확인하기",
        onDismiss = {
            showNoticeDialog = false
        },
        onConfirm = {
            showNoticeDialog = false
            screenshotViewModel.sendAction(ScreenshotAction.GoToNotice)
        }
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Divider)
                )
                BottomNavigationBar(
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
        ) {
            MainNavigationHost(
                screenshotIds = screenshotIds,
                navController = navController
            )
        }
    }
}
