package com.prography.home.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.prography.home.screen.HomeScreen
import com.prography.navigation.Screen

@Composable
fun HomeRoute(navController: NavController) {
    HomeScreen(
        onLogout = {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    )
}
