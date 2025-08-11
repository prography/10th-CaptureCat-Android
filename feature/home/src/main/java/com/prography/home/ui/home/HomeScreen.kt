package com.prography.home.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.collectAsLazyPagingItems
import com.prography.ui.component.UiCommonDialog

@Composable
fun HomeScreen(
    onNavigateToStorage: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()

    // 처음 접근 시 로그인 상태 체크
    LaunchedEffect(Unit) {
        viewModel.checkLoginStatusOnFirstAccess()
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HomeEffect.ShowError -> {
                    println("Home Error: ${effect.message}")
                }
                is HomeEffect.NavigateToStorage -> {
                    onNavigateToStorage()
                }
                is HomeEffect.OpenErrorReportChat -> {
                    // TODO: 실제 채팅 서비스 연결 구현
                    // 예: 카카오톡 채널, 구글 폼, 이메일 등
                    println("Open Error Report Chat")
                }
            }
        }
    }

    // 화면 재접근 시 Paging3 새로고침
    val lifecycleOwner = LocalLifecycleOwner.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) {
        pagingItems.refresh()
        viewModel.loadFavoriteImages()
    }

    HomeContent(
        state = state,
        onAction = { action ->
            viewModel.sendAction(action)
        },
        pagingItems = pagingItems
    )

    // 로그인 유도 다이얼로그
    if (state.showLoginDialog) {
        UiCommonDialog(
            isVisible = true,
            title = "로그인하기",
            message = "현재 게스트 모드로 이용 중이에요.\n게스트 모드에서는 최대 10장까지만 저장할 수 있어요.\n로그인하시겠습니까?",
            leftButtonText = "취소",
            rightButtonText = "확인",
            onDismiss = { viewModel.sendAction(HomeAction.HideLoginDialog) },
            onConfirm = { viewModel.sendAction(HomeAction.NavigateToLogin) }
        )
    }
}

// Navigation Helper Wrapper for Hilt injection
@dagger.hilt.android.lifecycle.HiltViewModel
class NavigationHelperWrapper @javax.inject.Inject constructor(
    val navigationHelper: NavigationHelper
) : androidx.lifecycle.ViewModel()
