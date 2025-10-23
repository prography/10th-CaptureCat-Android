package com.prography.auth.route.screen.contract

sealed interface LoginAction {
    data object ClickKakao : LoginAction
    data object ClickGoogle : LoginAction
    data object ClickSkip : LoginAction
    data object AccountLinkConfirm : LoginAction
    data object AccountLinkDismiss : LoginAction
}