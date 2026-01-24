package com.prography.setting.contract

data class SettingState(
    val isLoggedIn: Boolean = false,
    val nickname: String? = null,
    val email: String? = null,
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false,
    val showResetDialog: Boolean = false
)

sealed class SettingEffect {
    data class OpenExternalLink(val url: String) : SettingEffect()
}

sealed class SettingAction {
    object OnBackPressed : SettingAction()
    object OnLogin : SettingAction()
    object OnTagSetting : SettingAction()
    object OnImageDeleteSetting : SettingAction()
    object OnLogout : SettingAction()
    object OnNavigateToWithdraw : SettingAction()
    data class OnExternalLink(val url: String) : SettingAction()

    object OnClickLogout : SettingAction()
    object OnClickWithdraw : SettingAction()
    object OnClickNotice : SettingAction()
    object OnClickReset : SettingAction()
    object DismissLogoutDialog : SettingAction()
    object DismissWithdrawDialog : SettingAction()
    object DismissResetDialog : SettingAction()
    object OnReset : SettingAction()
}