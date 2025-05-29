package com.prography.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.prography.home.screen.HomeScaffold
import com.prography.navigation.Screen

fun NavController.navigateToHome(navOptions: NavOptions? = androidx.navigation.navOptions {  }){
    this.navigate(Screen.Home.route, navOptions)
}

fun NavGraphBuilder.homeScreen(){
    composable(route = Screen.Home.route){
        HomeScaffold()
    }
}