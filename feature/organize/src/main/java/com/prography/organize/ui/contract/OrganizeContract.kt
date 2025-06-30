package com.prography.organize.ui.contract

import com.prography.organize.model.OrganizeScreenshotItem

data class OrganizeState(
    val screenshots: List<OrganizeScreenshotItem> = emptyList(),
    val currentIndex: Int = 0,
    val organizeMode: OrganizeMode = OrganizeMode.BATCH,
    val availableTags: List<String> = emptyList(),
    val isLoading: Boolean = false
)

enum class OrganizeMode {
    BATCH,      // 한번에
    SINGLE      // 한장씩
}

sealed class OrganizeEffect {
    object NavigateUp : OrganizeEffect()
    object NavigateToComplete : OrganizeEffect()
    data class ShowAddTagBottomSheet(val screenshotId: String) : OrganizeEffect()
}

sealed class OrganizeAction {
    object OnNavigateUp : OrganizeAction()
    object OnComplete : OrganizeAction()
    data class OnModeChange(val mode: OrganizeMode) : OrganizeAction()
    data class OnScreenshotDelete(val screenshotId: String) : OrganizeAction()
    data class OnFavoriteToggle(val screenshotId: String, val isFavorite: Boolean) :
        OrganizeAction()

    data class OnTagToggle(val screenshotId: String, val tagText: String) : OrganizeAction()
    data class OnAddTag(val screenshotId: String) : OrganizeAction()
    data class OnCreateNewTag(val screenshotId: String, val tagText: String) : OrganizeAction()
    data class OnPageChange(val newIndex: Int) : OrganizeAction()
    object OnSaveScreenshots : OrganizeAction()
}