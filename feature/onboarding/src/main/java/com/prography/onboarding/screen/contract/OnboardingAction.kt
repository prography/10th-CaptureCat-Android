package com.prography.onboarding.screen.contract

sealed class OnboardingAction {
      object LoginClicked : OnboardingAction()
      object SkipClicked : OnboardingAction()
  }
