package com.prography.auth.presentation.login

sealed interface LoginEffect {
    data class ShowSnackBar(val message: String) : LoginEffect
    data object NavigateToHome : LoginEffect
}
