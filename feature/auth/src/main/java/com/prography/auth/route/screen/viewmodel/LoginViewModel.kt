package com.prography.auth.route.screen.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginEffect
import com.prography.auth.route.screen.contract.LoginState
import com.prography.auth.route.screen.contract.PendingAuth
import com.prography.auth.route.screen.contract.PendingLink
import com.prography.domain.model.LoginProvider
import com.prography.domain.usecase.auth.LoginNavigationResult
import com.prography.domain.usecase.auth.SocialLoginUseCase
import com.prography.domain.usecase.user.GetRecentLoginProviderUseCase
import com.prography.domain.usecase.user.GetStartTagScreenShownUseCase
import com.prography.domain.usecase.user.SetRecentLoginProviderUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.ui.R
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val app: Application,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val getStartTagScreenShownUseCase: GetStartTagScreenShownUseCase,
    private val getRecentLoginProvider: GetRecentLoginProviderUseCase,
    private val setRecentLoginProvider: SetRecentLoginProviderUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<LoginState, LoginEffect, LoginAction>(
    initialState = LoginState()
) {

    init {
        getRecentLoginProvider()
            .onEach { provider -> updateState { copy(recentLoginProvider = provider) } }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.ClickKakao -> emitEffect(LoginEffect.StartKakaoLogin)
            LoginAction.ClickGoogle -> emitEffect(LoginEffect.StartGoogleLogin)
            LoginAction.ClickSkip   -> handleSkipAction()
            LoginAction.AccountLinkConfirm -> confirmAccountLink()
            LoginAction.AccountLinkDismiss -> updateState { copy(pendingLink = null) }
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
            socialLoginUseCase(provider = "google", idToken = idToken).onSuccess { (navigationResult, loginResult) ->
                hideLoading()

                setRecentLoginProvider(LoginProvider.GOOGLE)
                MixpanelUtil.identify(userId)

                MixpanelUtil.track("complete_login", mapOf("login_method" to "google"))
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start, popUpTo = true))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main(), popUpTo = true))
                    }
                    LoginNavigationResult.NavigateToUpload -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "guest"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload, popUpTo = true))
                    }

                    is LoginNavigationResult.ShowAccountLinkDialog -> {
                        updateState {
                            copy(
                                pendingAuth = PendingAuth("google", idToken, null),
                                pendingLink = PendingLink(
                                    existingProvider = loginResult.existingProvider,
                                    linkToken = loginResult.linkToken
                                )
                            )
                        }
                    }
                }
            }.onFailure {
                hideLoading()
                showToast(app.getString(R.string.error_google_login_failed))
            }
        }
    }

    fun handleKakaoLoginSuccess(idToken: String, accessToken: String) {
        viewModelScope.launch {
            showLoading()

            val user = getKakaoUserInfo()
            if (user == null) {
                hideLoading()
                showToast(app.getString(R.string.error_kakao_user_info_failed))
                return@launch
            }

            val kakaoUserId = user.id.toString()
            Timber.d("user ${user.id} ${user}")

            socialLoginUseCase(provider = "kakao", idToken = idToken, accessToken = accessToken).onSuccess { (navigationResult, loginResult) ->
                hideLoading()
                setRecentLoginProvider(LoginProvider.KAKAO)
                MixpanelUtil.identify(kakaoUserId)

                MixpanelUtil.track("complete_login", mapOf("login_method" to "kakao"))
                when (navigationResult) {
                    LoginNavigationResult.NavigateToStartTag -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Start, popUpTo = true))
                    }

                    LoginNavigationResult.NavigateToHome -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "known"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Main(), popUpTo = true))
                    }

                    LoginNavigationResult.NavigateToUpload -> {
                        MixpanelUtil.track("complete_login", mapOf("user_type_before" to "guest"))
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload, popUpTo = true))
                    }

                    is LoginNavigationResult.ShowAccountLinkDialog -> {
                        updateState {
                            copy(
                                pendingAuth = PendingAuth("kakao", idToken, accessToken),
                                pendingLink = PendingLink(
                                    existingProvider = loginResult.existingProvider,
                                    linkToken = loginResult.linkToken
                                )
                            )
                        }
                    }
                }
            }.onFailure {
                hideLoading()
                showToast(app.getString(R.string.error_kakao_login_failed))
            }
        }
    }

    private fun confirmAccountLink() = viewModelScope.launch {
        val pa = uiState.value.pendingAuth
        val pl = uiState.value.pendingLink
        if (pa == null || pl == null) {
            updateState { copy(pendingLink = null) }; return@launch
        }

        showLoading()
        socialLoginUseCase(
            idToken = pa.idToken,
            provider = pa.provider,
            accessToken = pa.accessToken,
            linkToken = pl.linkToken
        ).onSuccess { (nav, _) ->
            updateState { copy(pendingLink = null) }
            hideLoading()
            when (nav) {
                LoginNavigationResult.NavigateToStartTag ->
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Start, popUpTo = true))

                LoginNavigationResult.NavigateToHome ->
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Main(), popUpTo = true))

                LoginNavigationResult.NavigateToUpload ->
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Upload, popUpTo = true))

                is LoginNavigationResult.ShowAccountLinkDialog -> {
                    updateState {
                        copy(pendingLink = PendingLink(nav.existingProvider, nav.linkToken))
                    }
                }
            }
        }.onFailure {
            hideLoading()
            showToast(app.getString(R.string.error_account_link_failed))
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