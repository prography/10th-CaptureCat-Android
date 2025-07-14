package com.prography.home.ui.storage.contract

sealed interface ScreenshotAction {
    data class ToggleSelect(val id: String) : ScreenshotAction
    object SelectAll : ScreenshotAction
    object CancelSelection : ScreenshotAction
    object DeleteSelected : ScreenshotAction
    object ConfirmDelete : ScreenshotAction
    object DismissDeleteDialog : ScreenshotAction
    object OrganizeSelected : ScreenshotAction
    object OrganizeCompleted : ScreenshotAction
    object RefreshScreenshots : ScreenshotAction
    object ShowDeleteDialog : ScreenshotAction
    object NavigateToLogin : ScreenshotAction
}
