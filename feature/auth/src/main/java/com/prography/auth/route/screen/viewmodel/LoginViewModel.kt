package com.prography.auth.route.screen.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.contract.LoginState
import com.prography.domain.usecase.auth.SocialLoginUseCase
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialLoginUseCase: SocialLoginUseCase
) : BaseComposeViewModel<LoginState, LoginEffect, LoginAction>(
    initialState = LoginState()
) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.ClickKakao -> emitEffect(LoginEffect.StartKakaoLogin)
            LoginAction.ClickGoogle -> emitEffect(LoginEffect.StartGoogleLogin)
            LoginAction.ClickSkip -> emitEffect(LoginEffect.NavigateToOnboarding)
        }
    }

    fun handleGoogleLoginSuccess(idToken: String) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }
                val result = socialLoginUseCase("google", idToken)
                updateState { copy(isLoading = false) }

                if (result.isSuccess) {
                    emitEffect(LoginEffect.NavigateToOnboarding)
                } else {
                    emitEffect(LoginEffect.ShowError("구글 로그인에 실패했습니다"))
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                emitEffect(LoginEffect.ShowError("구글 로그인 중 오류가 발생했습니다"))
            }
        }
    }

    fun handleKakaoLoginSuccess(accessToken: String) {
        viewModelScope.launch {
            socialLoginUseCase("kakao", accessToken).onSuccess {
                emitEffect(LoginEffect.NavigateToOnboarding)
            }.onFailure {
                showToast("카카오 로그인 중 오류가 발생했습니다")
            }
        }
    }
}