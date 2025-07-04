package com.prography.onboarding.navigation

import androidx.compose.runtime.Composable
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.screen.ui.InitOnboardingScreen
import com.prography.onboarding.screen.ui.OnboardingScreen

@Composable
fun InitOnboardingRoute(navigationHelper: NavigationHelper) {
    InitOnboardingScreen(navigationHelper = navigationHelper)
}