package com.prography.home.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.collectAsLazyPagingItems
import com.prography.ui.component.UiCommonDialog
import androidx.core.net.toUri
import com.prography.util.MixpanelUtil

@Composable
fun HomeScreen(
    onNavigateToStorage: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val context = LocalContext.current

    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()

    // 처음 접근 시 로그인 상태 체크 및 인기 태그 로드
    LaunchedEffect(Unit) {
        MixpanelUtil.track("view_home")
        viewModel.checkLoginStatusOnFirstAccess()
        viewModel.loadMostUsedTags()
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
                is HomeEffect.OpenExternalLink -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    var isFabMenuOpen by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        HomeContent(
            state = state,
            onAction = { action -> viewModel.sendAction(action) },
            pagingItems = pagingItems
        )
        CaptureCatFab(
            onUploadClick = { viewModel.sendAction(HomeAction.NavigateToStorageUpload) },
            onOrganizeClick = { viewModel.sendAction(HomeAction.NavigateToStorageOrganize) }
        )
    }

    // 로그인 유도 다이얼로그
    if (state.showLoginDialog) {
        UiCommonDialog(
            isVisible = true,
            title = stringResource(com.prography.ui.R.string.login_title),
            message = stringResource(com.prography.ui.R.string.login_message),
            leftButtonText = stringResource(com.prography.ui.R.string.common_cancel),
            rightButtonText = stringResource(com.prography.ui.R.string.common_confirm),
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
