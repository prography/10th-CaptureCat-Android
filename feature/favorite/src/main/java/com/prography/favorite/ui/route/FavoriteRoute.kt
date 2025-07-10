package com.prography.favorite.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.favorite.ui.FavoriteContent
import com.prography.favorite.ui.contract.FavoriteEffect
import com.prography.favorite.ui.viewmodel.FavoriteViewModel
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FavoriteRoute(
    navigationHelper: NavigationHelper,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FavoriteEffect.ShowError -> {
                }
            }
        }
    }

    FavoriteContent(
        state = state,
        onAction = { action ->
            viewModel.sendAction(action)
        }
    )
}