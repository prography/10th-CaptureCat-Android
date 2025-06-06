package com.prography.onboarding.navigation

import androidx.compose.runtime.Composable
import com.prography.navigation.NavigationHelper
import com.prography.onboarding.screen.ui.OnboardingScreen

@Composable
fun OnboardingRoute(navigationHelper: NavigationHelper) {
    OnboardingScreen(navigationHelper = navigationHelper)
}