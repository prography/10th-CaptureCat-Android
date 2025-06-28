package com.prography.navigation

import android.net.Uri
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
    data class Organize(
        val screenshotIds: List<String> = emptyList()
    ) : AppRoute

    @Serializable
    data object Start : AppRoute
}
