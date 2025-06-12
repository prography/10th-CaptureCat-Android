package com.prography.navigation

import kotlinx.serialization.Serializable

sealed interface Route

sealed interface AppRoute : Route {

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Main : AppRoute

    @Serializable
    data object Organize : AppRoute
}
