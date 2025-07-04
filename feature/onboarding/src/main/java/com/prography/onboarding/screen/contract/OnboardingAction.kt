package com.prography.onboarding.screen.contract

sealed class OnboardingAction {
    object LoginClicked : OnboardingAction()
    object SkipClicked : OnboardingAction()
    data class PageChanged(val page: Int) : OnboardingAction()
    object NextClicked : OnboardingAction()
    object StartClicked : OnboardingAction()
}
