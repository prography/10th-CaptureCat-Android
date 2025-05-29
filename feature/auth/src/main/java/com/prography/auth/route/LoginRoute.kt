package com.prography.auth.route

import androidx.compose.runtime.Composable
import com.prography.auth.route.screen.LoginScreen

@Composable
fun LoginRoute(navigateToHome: () -> Unit) {
    LoginScreen(
        onLoginSuccess = navigateToHome
    )
}
