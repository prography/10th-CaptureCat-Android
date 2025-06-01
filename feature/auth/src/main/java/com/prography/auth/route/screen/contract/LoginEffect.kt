package com.prography.auth.route.screen.contract

sealed interface LoginEffect {
    object NavigateToHome : LoginEffect
    object StartKakaoLogin : LoginEffect
    object StartGoogleLogin : LoginEffect
}