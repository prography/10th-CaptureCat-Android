package com.prography.auth.route.screen.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.viewmodel.LoginViewModel
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigationHelper: NavigationHelper
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                LoginEffect.StartKakaoLogin -> {
                    // 카카오 SDK 호출
                }
                LoginEffect.StartGoogleLogin -> {
                    // 구글 SDK 호출
                }
                LoginEffect.NavigateToHome -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                }
            }
        }
    }

    LoginContent(
        state = state,
        onAction = { viewModel.sendAction(it) }
    )
}