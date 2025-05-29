package com.prography.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.prography.home.navigation.homeScreen
import com.prography.home.navigation.navigateToHome
import com.prography.auth.route.navigation.loginScreen
import com.prography.navigation.Screen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        loginScreen(navigateToHome = {
            navController.navigateToHome(navOptions{
                popUpTo(Screen.Home.route){ inclusive = true }
            })
        })
        homeScreen()
    }
}
