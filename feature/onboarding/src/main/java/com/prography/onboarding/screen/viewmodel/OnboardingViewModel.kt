package com.prography.onboarding.screen.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.onboarding.screen.contract.OnboardingEffect
import com.prography.onboarding.screen.contract.OnboardingState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingShownUseCase: SetOnboardingShownUseCase
) : BaseComposeViewModel<OnboardingState, OnboardingEffect, OnboardingAction>(
    initialState = OnboardingState()
) {
    override fun handleAction(action: OnboardingAction) {
        viewModelScope.launch {
            setOnboardingShownUseCase(true)

            when (action) {
                OnboardingAction.LoginClicked -> emitEffect(OnboardingEffect.NavigateToLogin)
                OnboardingAction.SkipClicked -> emitEffect(OnboardingEffect.NavigateToStart)
            }
        }
    }
}