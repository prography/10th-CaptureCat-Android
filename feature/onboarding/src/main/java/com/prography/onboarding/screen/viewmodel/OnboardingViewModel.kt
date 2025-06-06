package com.prography.onboarding.screen.viewmodel

import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.onboarding.screen.contract.OnboardingEffect
import com.prography.onboarding.screen.contract.OnboardingState
import com.prography.ui.BaseComposeViewModel
import javax.inject.Inject


class OnboardingViewModel @Inject constructor() : BaseComposeViewModel<OnboardingState, OnboardingEffect, OnboardingAction>(
    initialState = OnboardingState()
) {
    override fun handleAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.LoginClicked -> emitEffect(OnboardingEffect.NavigateToLogin)
            OnboardingAction.SkipClicked -> emitEffect(OnboardingEffect.NavigateToHome)
        }
    }
}