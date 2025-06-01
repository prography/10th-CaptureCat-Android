package com.prography.navigation

sealed class NavigationEvent {
    data class To(val route: Route, val popUpTo: Boolean = false) : NavigationEvent()
    data object Up : NavigationEvent()
    data class TopLevelTo(val route: Route) : NavigationEvent()
    data class BottomBarTo(val route: Route) : NavigationEvent()
}
