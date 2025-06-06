package com.prography.onboarding.screen.contract

sealed class OnboardingEffect {
      object NavigateToLogin : OnboardingEffect()
      object NavigateToHome : OnboardingEffect()
  }
