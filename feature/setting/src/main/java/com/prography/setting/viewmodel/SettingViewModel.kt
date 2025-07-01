package com.prography.setting.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.auth.GetAuthTokenUseCase
import com.prography.setting.contract.SettingAction
import com.prography.setting.contract.SettingEffect
import com.prography.setting.contract.SettingState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getAuthTokenUseCase: GetAuthTokenUseCase
) : BaseComposeViewModel<SettingState, SettingEffect, SettingAction>(
    initialState = SettingState()
) {

    init {
        checkLoginStatus()
    }

    override fun handleAction(action: SettingAction) {
        when (action) {
            SettingAction.OnLogin -> {
                emitEffect(SettingEffect.NavigateToLogin)
            }

            SettingAction.OnNavigateUp -> {
                emitEffect(SettingEffect.NavigateUp)
            }

            SettingAction.OnLogout -> {
                updateState { copy(showLogoutDialog = false) }
                logout()
            }

            SettingAction.OnNavigateToWithdraw -> {
                updateState { copy(showWithdrawDialog = false) }
                emitEffect(SettingEffect.NavigateToWithdraw)
            }

            is SettingAction.OnConfirmWithdraw -> {
                withdrawUser(action.reason)
            }

            is SettingAction.OnExternalLink -> {
                Timber.d("Opening external link: ${action.url}")
            }

            SettingAction.OnClickLogout -> updateState { copy(showLogoutDialog = true) }
            SettingAction.DismissLogoutDialog -> updateState { copy(showLogoutDialog = false) }

            SettingAction.OnClickWithdraw -> updateState { copy(showWithdrawDialog = true) }
            SettingAction.DismissWithdrawDialog -> updateState { copy(showWithdrawDialog = false) }
        }
    }

    private fun checkLoginStatus() {
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
    }

    private fun logout() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            runCatching {
                // TODO: 로그아웃 UseCase 구현 후 호출
                Timber.d("User logout requested")
                emitEffect(SettingEffect.ShowLogoutSuccess)
                emitEffect(SettingEffect.NavigateToLogin)
            }.onFailure { exception ->
                Timber.e(exception, "Failed to logout")
            }.also {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun withdrawUser(reason: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            runCatching {
                // TODO: 회원탈퇴 UseCase 구현 후 호출
                Timber.d("User withdrawal requested with reason: $reason")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to withdraw user")
            }.also {
                updateState { copy(isLoading = false) }
            }
        }
    }
}