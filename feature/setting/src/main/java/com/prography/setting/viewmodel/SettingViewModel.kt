package com.prography.setting.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.auth.GetAuthTokenUseCase
import com.prography.domain.usecase.auth.LogoutUseCase
import com.prography.domain.usecase.screenshot.DeleteAllDataUseCase
import com.prography.domain.usecase.user.GetUserInfoUseCase
import com.prography.setting.contract.SettingAction
import com.prography.setting.contract.SettingEffect
import com.prography.setting.contract.SettingState
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getAuthTokenUseCase: GetAuthTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val deleteAllScreenshotsUseCase: DeleteAllDataUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<SettingState, SettingEffect, SettingAction>(
    initialState = SettingState()
) {
    init {
        loadUserInfo()
    }

    override fun handleAction(action: SettingAction) {
        when (action) {
            SettingAction.OnLogin -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Login)
                )
            }
            SettingAction.OnTagSetting -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.TagSetting)
                )
            }
            SettingAction.OnImageDeleteSetting -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.ImageDeleteSetting)
                )
            }
            SettingAction.OnLogout -> {
                updateState { copy(showLogoutDialog = false) }
                logout()
            }

            SettingAction.OnNavigateToWithdraw -> {
                updateState { copy(showWithdrawDialog = false) }
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.SettingRoute.Withdraw)
                )
            }

            is SettingAction.OnExternalLink -> {
                Timber.d("Opening external link: ${action.url}")
                emitEffect(SettingEffect.OpenExternalLink(action.url))
            }

            SettingAction.OnClickLogout -> updateState { copy(showLogoutDialog = true) }
            SettingAction.DismissLogoutDialog -> updateState { copy(showLogoutDialog = false) }
            SettingAction.OnClickWithdraw -> updateState { copy(showWithdrawDialog = true) }
            SettingAction.DismissWithdrawDialog -> updateState { copy(showWithdrawDialog = false) }
            SettingAction.OnClickReset -> updateState { copy(showResetDialog = true) }
            SettingAction.DismissResetDialog -> updateState { copy(showResetDialog = false) }
            SettingAction.OnReset -> {
                updateState { copy(showResetDialog = false) }

                viewModelScope.launch {
                    deleteAllScreenshotsUseCase()
                    showToast("캡처캣의 데이터가 삭제 되었습니다.")
                }
            }
            SettingAction.OnBackPressed -> {
                navigationHelper.navigate(NavigationEvent.Up)
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            runCatching {
                val isLoggedIn = getAuthTokenUseCase.isLoggedIn()
                Timber.d("Login status checked: $isLoggedIn")
                updateState { copy(isLoggedIn = isLoggedIn) }
            }.onFailure { exception ->
                Timber.e(exception, "Failed to check login status")
                updateState { copy(isLoggedIn = false) }
            }
        }

        viewModelScope.launch {
            if (currentState.isLoggedIn) {
                getUserInfoUseCase()
                    .onSuccess { info ->
                        Timber.d("User info loaded: $info")
                        updateState { copy(nickname = info.nickname, email = info.email) }
                    }
                    .onFailure { e -> Timber.e(e, "Failed to load user info") }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            runCatching {
                logoutUseCase.invoke()
            }.onSuccess {
                Timber.d("User logout successful")

                MixpanelUtil.track("logout")
                MixpanelUtil.reset()

                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Main(), popUpTo = true)
                )
            }.onFailure { exception ->
                Timber.e(exception, "Failed to logout")
            }.also {
                updateState { copy(isLoading = false) }
            }
        }
    }
}