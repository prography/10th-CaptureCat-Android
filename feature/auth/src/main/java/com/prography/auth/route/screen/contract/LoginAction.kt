package com.prography.auth.route.screen.contract

sealed interface LoginAction {
    object ClickKakao : LoginAction
    object ClickGoogle : LoginAction
    object ClickSkip : LoginAction
}
