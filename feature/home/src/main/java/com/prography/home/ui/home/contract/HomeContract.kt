package com.prography.home.ui.home.contract

import com.prography.domain.model.UiScreenshotModel

// Define Actions
sealed class HomeAction {
    object LoadScreenshots : HomeAction()
    data class SelectTag(val tag: String) : HomeAction()
    object NavigateToSettings : HomeAction()
}

// Define Effects
sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
}

// Define UI State
data class HomeState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val tags: List<String> = listOf("전체"),
    val selectedTag: String = "전체"
)