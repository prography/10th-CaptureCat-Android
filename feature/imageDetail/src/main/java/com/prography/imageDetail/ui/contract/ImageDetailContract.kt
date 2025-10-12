package com.prography.imageDetail.ui.contract

import com.prography.domain.model.TagModel
import com.prography.domain.model.UiScreenshotModel

data class ImageDetailState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val currentIndex: Int = 0,
    val currentScreenshot: UiScreenshotModel? = null,
    val availableTags: List<String> = emptyList(),     // 문자열 풀(자동완성용이면 유지)
    val userTags: List<TagModel> = emptyList(),        // 유저가 보유한 태그
    val isUserTagsExpanded: Boolean = false,           // 더보기 토글
    val isTagEditBottomSheetVisible: Boolean = false,
    val isTagAddBottomSheetVisible: Boolean = false,   // “태그 추가” 2단계 시트
    val isDeleteDialogVisible: Boolean = false,
    val newTagText: String = "",
    val tagErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val pendingAddTags: List<TagModel> = emptyList()   // 2단계 시트에서 선택 중
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

    object OnShowTagEditBottomSheet : ImageDetailAction()
    object OnHideTagEditBottomSheet : ImageDetailAction()

    // 1단계(태그 수정) 화면
    data class OnTagDelete(val tag: TagModel) : ImageDetailAction()
    object OnShowTagAddBottomSheet : ImageDetailAction()
    object OnToggleUserTagsExpanded : ImageDetailAction()
    data class OnClickUserTag(val tag: TagModel) : ImageDetailAction() // 기존태그 보기에서 추가

    // 2단계(태그 추가) 시트
    data class OnNewTagTextChange(val text: String) : ImageDetailAction()
    object OnAddNewTag : ImageDetailAction() // 입력창에서 바로 추가
    data class OnTogglePendingTag(val tag: TagModel) : ImageDetailAction()
    object OnConfirmPendingTags : ImageDetailAction()
    object OnHideTagAddBottomSheet : ImageDetailAction()

    object OnDeleteScreenshot : ImageDetailAction()
    object OnShowDeleteDialog : ImageDetailAction()
    object OnHideDeleteDialog : ImageDetailAction()
    object OnConfirmDelete : ImageDetailAction()
}