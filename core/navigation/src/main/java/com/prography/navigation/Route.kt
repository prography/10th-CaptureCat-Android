package com.prography.navigation

import android.net.Uri
import kotlinx.serialization.Serializable

sealed interface Route

sealed interface AppRoute : Route {

    @Serializable
    data object InitOnboarding : AppRoute

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

    @Serializable
    data class ImageDetail(
        val screenshotIds: List<String> = emptyList(),
        val currentIndex: Int = 0
    ) : AppRoute

    @Serializable
    sealed interface SettingRoute : AppRoute {
        @Serializable
        data object Setting : SettingRoute

        @Serializable
        data object Withdraw : SettingRoute
    }
}
