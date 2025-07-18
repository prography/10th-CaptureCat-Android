package com.prography.auth.route.screen.contract

sealed interface LoginEffect {
    object NavigateToStart : LoginEffect
    object NavigateToOnboarding : LoginEffect
    object StartKakaoLogin : LoginEffect
    object StartGoogleLogin : LoginEffect
}