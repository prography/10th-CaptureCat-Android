package com.prography.home.ui.home.contract

import com.prography.domain.model.UiScreenshotModel

// Define Actions
sealed class HomeAction {
    object NavigateToFavorite : HomeAction()
    object NavigateToStorage : HomeAction()
    object NavigateToSearch : HomeAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : HomeAction()
    object ShowLoginDialog : HomeAction()
    object HideLoginDialog : HomeAction()
    object NavigateToLogin : HomeAction()
    object OnErrorReportClick : HomeAction() // 오류 제보 클릭
    object DismissErrorReportBanner : HomeAction() // 오류 제보 배너 닫기
}

// Define Effects
sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
    object NavigateToStorage : HomeEffect()
    data class OpenExternalLink(val url: String) : HomeEffect()
}

// Define UI State
data class HomeState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val favoriteScreenshots: List<UiScreenshotModel> = emptyList(),
    val showLoginDialog: Boolean = false,
    val showErrorReportBanner: Boolean = true, // 오류 제보 배너 표시 여부
    val error: String? = null
)