package com.prography.auth.presentation.login

sealed interface LoginAction {
    data object ClickKakaoLogin : LoginAction
    data object ClickGoogleLogin : LoginAction
}
