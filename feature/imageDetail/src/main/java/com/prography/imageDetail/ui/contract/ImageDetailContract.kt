package com.prography.imageDetail.ui.contract

import com.prography.domain.model.UiScreenshotModel

data class ImageDetailState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val currentIndex: Int = 0,
    val currentScreenshot: UiScreenshotModel? = null,
    val availableTags: List<String> = emptyList(),
    val isTagEditBottomSheetVisible: Boolean = false,
    val newTagText: String = "",
    val isLoading: Boolean = false
)

sealed class ImageDetailEffect {
    object NavigateBack : ImageDetailEffect()
    object ShowDeleteConfirmation : ImageDetailEffect()
    data class ShowError(val message: String) : ImageDetailEffect()
    object ScreenshotDeleted : ImageDetailEffect()
}

sealed class ImageDetailAction {
    object OnNavigateBack : ImageDetailAction()
    data class OnPageChange(val newIndex: Int) : ImageDetailAction()
    object OnToggleFavorite : ImageDetailAction()
    object OnShowTagEditBottomSheet : ImageDetailAction()
    object OnHideTagEditBottomSheet : ImageDetailAction()
    data class OnTagDelete(val tag: String) : ImageDetailAction()
    data class OnNewTagTextChange(val text: String) : ImageDetailAction()
    object OnAddNewTag : ImageDetailAction()
    object OnDeleteScreenshot : ImageDetailAction()
    object OnConfirmDelete : ImageDetailAction()
}