package com.prography.home.ui.storage.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.storage.contract.ScreenshotEffect
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScreenshotOrganizeScreen(
    viewModel: ScreenshotViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                ScreenshotEffect.ShowDeleteToast -> {

                }
            }
        }
    }

    ScreenshotOrganizeContent(
        state = state,
        onAction = { viewModel.sendAction(it) }
    )
}
