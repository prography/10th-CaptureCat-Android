package com.prography.home.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.home.contract.HomeEffect
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun HomeScreen(
    onNavigateToStorage: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HomeEffect.ShowError -> {
                    println("Home Error: ${effect.message}")
                }
                is HomeEffect.NavigateToStorage -> {
                    onNavigateToStorage()
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
}
