package com.prography.onboarding.screen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.screen.contract.OnboardingEffect
import com.prography.onboarding.screen.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    navigationHelper: NavigationHelper,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val effectFlow = viewModel.effect

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                OnboardingEffect.NavigateToHome -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                }

                OnboardingEffect.NavigateToLogin -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Login, popUpTo = true))
                }
            }
        }
    }

    OnboardingContent(
        onAction = { viewModel.sendAction(it) }
    )
}
