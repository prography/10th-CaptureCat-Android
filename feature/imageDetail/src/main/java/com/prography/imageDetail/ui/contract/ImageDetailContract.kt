package com.prography.imageDetail.ui.contract

import com.prography.domain.model.TagModel
import com.prography.domain.model.UiScreenshotModel
import com.prography.imageDetail.ui.content.Sheet

data class ImageDetailState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val currentIndex: Int = 0,
    val currentScreenshot: UiScreenshotModel? = null,
    val availableTags: List<String> = emptyList(),     // 문자열 풀(자동완성용이면 유지)
    val userTags: List<TagModel> = emptyList(),        // 유저가 보유한 태그
    val isUserTagsExpanded: Boolean = false,           // 더보기 토글
    val isDeleteDialogVisible: Boolean = false,
    val newTagText: String = "",
    val tagErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val currentSheet: Sheet = Sheet.None
)

sealed class ImageDetailEffect {
    object NavigateBack : ImageDetailEffect()
    data class ShowError(val message: String) : ImageDetailEffect()
    object ScreenshotDeleted : ImageDetailEffect()
}

sealed class ImageDetailAction {
    object OnNavigateBack : ImageDetailAction()
    data class OnPageChange(val newIndex: Int) : ImageDetailAction()
    object OnToggleFavorite : ImageDetailAction()

    data class ShowSheet(val sheet: Sheet): ImageDetailAction()
    data object HideSheet: ImageDetailAction()

    // 1단계(태그 수정) 화면
    data class OnTagDelete(val tag: TagModel) : ImageDetailAction()
    object OnToggleUserTagsExpanded : ImageDetailAction()
    data class OnClickUserTag(val tag: TagModel) : ImageDetailAction() // 기존태그 보기에서 추가

    // 2단계(태그 추가) 시트
    data class OnNewTagTextChange(val text: String) : ImageDetailAction()
    object OnAddNewTag : ImageDetailAction() // 입력창에서 바로 추가

    object OnDeleteScreenshot : ImageDetailAction()
    object OnShowDeleteDialog : ImageDetailAction()
    object OnHideDeleteDialog : ImageDetailAction()
    object OnConfirmDelete : ImageDetailAction()
}