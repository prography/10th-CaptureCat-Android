package com.prography.home.ui.storage.contract

sealed interface ScreenshotAction {
    data class ToggleSelect(val id: String) : ScreenshotAction
    data class SelectAll(val allIds: List<String>) : ScreenshotAction

    object Back : ScreenshotAction
    object CancelSelection : ScreenshotAction
    object DeleteSelected : ScreenshotAction
    object ConfirmDelete : ScreenshotAction
    object DismissDeleteDialog : ScreenshotAction
    object OrganizeSelected : ScreenshotAction
    object OrganizeCompleted : ScreenshotAction
    object RefreshScreenshots : ScreenshotAction
    object ShowDeleteDialog : ScreenshotAction
    object NavigateToLogin : ScreenshotAction
    object LoadMoreScreenshots : ScreenshotAction
}
