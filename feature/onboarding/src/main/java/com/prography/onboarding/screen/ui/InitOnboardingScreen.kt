package com.prography.onboarding.screen.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.screen.contract.OnboardingEffect
import com.prography.onboarding.screen.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun InitOnboardingScreen(
    navigationHelper: NavigationHelper,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val effectFlow = viewModel.effect

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                OnboardingEffect.NavigateToStart -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Start, popUpTo = true))
                }

                OnboardingEffect.NavigateToLogin -> {
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Login, popUpTo = true))
                }
            }
        }
    }

    InitOnboardingContent(
        viewModel = viewModel
    )
}
