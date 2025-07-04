package com.prography.auth.route.screen.viewmodel

import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.contract.LoginState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseComposeViewModel<LoginState, LoginEffect, LoginAction>(
    initialState = LoginState()
) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.ClickKakao -> emitEffect(LoginEffect.StartKakaoLogin)
            LoginAction.ClickGoogle -> emitEffect(LoginEffect.StartGoogleLogin)
            LoginAction.ClickSkip -> emitEffect(LoginEffect.NavigateToOnboarding)
        }
    }
}