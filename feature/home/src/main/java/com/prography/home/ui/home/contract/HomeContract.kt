package com.prography.home.ui.home.contract

import com.prography.domain.model.UiScreenshotModel

// Define Actions
sealed class HomeAction {
    object NavigateToSettings : HomeAction()
    object NavigateToFavorite : HomeAction()
    object NavigateToStorage : HomeAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : HomeAction()
    object ShowLoginDialog : HomeAction()
    object HideLoginDialog : HomeAction()
    object NavigateToLogin : HomeAction()
}

// Define Effects
sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
    object NavigateToStorage : HomeEffect()
}

// Define UI State
data class HomeState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val favoriteScreenshots: List<UiScreenshotModel> = emptyList(),
    val showLoginDialog: Boolean = false
)