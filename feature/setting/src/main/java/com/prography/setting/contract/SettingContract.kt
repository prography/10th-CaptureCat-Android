package com.prography.setting.contract

data class SettingState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false
)

sealed class SettingEffect {
    object NavigateUp : SettingEffect()
    object NavigateToLogin : SettingEffect()
    object NavigateToWithdraw : SettingEffect()
    object ShowLogoutSuccess : SettingEffect()
    object ShowWithdrawSuccess : SettingEffect()
}

sealed class SettingAction {
    object OnNavigateUp : SettingAction()
    object OnLogin : SettingAction()
    object OnLogout : SettingAction()
    object OnNavigateToWithdraw : SettingAction()
    data class OnConfirmWithdraw(val reason: String) : SettingAction()
    data class OnExternalLink(val url: String) : SettingAction()

    object OnClickLogout : SettingAction()
    object OnClickWithdraw : SettingAction()
    object DismissLogoutDialog : SettingAction()
    object DismissWithdrawDialog : SettingAction()
}