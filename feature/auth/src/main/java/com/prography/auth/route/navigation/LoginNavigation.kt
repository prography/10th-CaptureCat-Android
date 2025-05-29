package com.prography.auth.route.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.prography.auth.route.LoginRoute
import com.prography.navigation.Screen

fun NavController.navigateToLogin(navOptions: NavOptions? = androidx.navigation.navOptions {  }){
    this.navigate(Screen.Login.route, navOptions)
}

fun NavGraphBuilder.loginScreen(navigateToHome: () -> Unit) {
    composable(route = Screen.Login.route){
        LoginRoute(navigateToHome)
    }
}