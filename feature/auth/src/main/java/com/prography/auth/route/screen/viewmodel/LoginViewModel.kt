package com.prography.auth.route.screen.viewmodel

import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
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
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import timber.log.Timber

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

    fun handleGoogleLoginSuccess(idToken: String, userId: String) {
        viewModelScope.launch {
            showLoading()
            socialLoginUseCase("google", idToken).onSuccess { (navigationResult, loginResult) ->
                hideLoading()

                // Mixpanel 사용자 식별 - Google User ID 사용
                MixpanelUtil.identify(userId)

                MixpanelUtil.track("complete_login", mapOf("login_method" to "google"))
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
                    }
                    LoginNavigationResult.NavigateToUpload -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "guest"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload))
                    }
                }
            }.onFailure {
                hideLoading()
                showToast("구글 로그인에 실패했습니다")
            }
        }
    }

    fun handleKakaoLoginSuccess(idToken: String, accessToken: String) {
        viewModelScope.launch {
            showLoading()

            val user = getKakaoUserInfo()
            if (user == null) {
                hideLoading()
                showToast("카카오 사용자 정보를 가져오는 데 실패했어요")
                return@launch
            }

            val kakaoUserId = user.id.toString()
            Timber.d("user ${user.id} ${user}")

            socialLoginUseCase("kakao", idToken, accessToken).onSuccess { (navigationResult, loginResult) ->
                hideLoading()

                MixpanelUtil.identify(kakaoUserId)

                MixpanelUtil.track("complete_login", mapOf("login_method" to "kakao"))
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main))
                    }

                    LoginNavigationResult.NavigateToUpload -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "guest"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload))
                    }
                }
            }.onFailure {
                hideLoading()
                showToast("카카오 로그인 중 오류가 발생했습니다")
            }
        }
    }

    private suspend fun getKakaoUserInfo(): com.kakao.sdk.user.model.User? =
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    cont.resume(null) { _, _, _ -> }  // 실패 시 null 반환
                } else {
                    cont.resume(user) { _, _, _ -> }
                }
            }
        }
}