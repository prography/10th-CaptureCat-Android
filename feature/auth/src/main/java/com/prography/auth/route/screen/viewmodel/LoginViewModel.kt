package com.prography.auth.route.screen.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.contract.LoginState
import com.prography.domain.usecase.auth.LoginNavigationResult
import com.prography.domain.usecase.auth.SocialLoginUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialLoginUseCase: SocialLoginUseCase,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<LoginState, LoginEffect, LoginAction>(
    initialState = LoginState()
) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.ClickKakao -> emitEffect(LoginEffect.StartKakaoLogin)
            LoginAction.ClickGoogle -> emitEffect(LoginEffect.StartGoogleLogin)
            LoginAction.ClickSkip -> handleSkipAction()
        }
    }

    private fun handleSkipAction() {
        viewModelScope.launch {
            val hasSeenStartTagScreen = getStartTagScreenShownUseCase().first()

            if (hasSeenStartTagScreen) {
                // 시작 태그 화면을 본 유저: 로그인 화면만 닫기
                navigationHelper.navigate(NavigationEvent.Up)
            } else {
                // 시작 태그 화면을 안 본 유저: 온보딩으로 이동
                navigationHelper.navigate(NavigationEvent.To(AppRoute.Onboarding))
            }
        }
    }

    fun handleGoogleLoginSuccess(idToken: String) {
        viewModelScope.launch {
            showLoading()
            socialLoginUseCase("google", idToken).onSuccess { navigationResult ->
                hideLoading()
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                    }
                    LoginNavigationResult.NavigateToUpload -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload))
                    }
                }
            }.onFailure {
                hideLoading()
                showToast("구글 로그인에 실패했습니다")
            }
        }
    }

    fun handleKakaoLoginSuccess(accessToken: String) {
        viewModelScope.launch {
            showLoading()
            socialLoginUseCase("kakao", accessToken).onSuccess { navigationResult ->
                hideLoading()
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main))
                    }
                    LoginNavigationResult.NavigateToUpload -> {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload))
                    }
                }
            }.onFailure {
                hideLoading()
                showToast("카카오 로그인 중 오류가 발생했습니다")
            }
        }
    }
}