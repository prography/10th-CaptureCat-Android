package com.prography.home.ui.home.contract

import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel

// Define Actions
sealed class HomeAction {
    object NavigateToFavorite : HomeAction()
    data object NavigateToStorageUpload : HomeAction()
    data object NavigateToStorageOrganize : HomeAction()
    object NavigateToMyPage : HomeAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : HomeAction()
    object ShowLoginDialog : HomeAction()
    object HideLoginDialog : HomeAction()
    object NavigateToLogin : HomeAction()
    object OnErrorReportClick : HomeAction() // 오류 제보 클릭
    object DismissErrorReportBanner : HomeAction() // 오류 제보 배너 닫기
    data class OnTabSelected(val tabTag: String) : HomeAction() // 탭 선택
    object NavigateToTagSetting : HomeAction() // 태그 설정으로 이동


    data class OnArriveWithIds(val ids: List<String>) : HomeAction()
    data class OnDeleteChoiceConfirm(val ids: List<String>) : HomeAction()
    object OnDeleteChoiceLater : HomeAction()
    data class OnSystemDeleteResult(val successCount: Int) : HomeAction()
}

// Define Effects
sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
    object NavigateToStorage : HomeEffect()
    data class OpenExternalLink(val url: String) : HomeEffect()
    data class ShowDeleteChoiceBottomSheet(val ids: List<String>) : HomeEffect()
    data class RequestSystemDelete(val uris: List<android.net.Uri>) : HomeEffect()
}

// Define UI State
data class HomeState(
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val favoriteScreenshots: List<UiScreenshotModel> = emptyList(),
    val showLoginDialog: Boolean = false,
    val showErrorReportBanner: Boolean = true, // 오류 제보 배너 표시 여부
    val error: String? = null,
    val popularTags: List<TagWithCount> = emptyList(), // 인기 태그 목록
    val selectedTab: String = "전체", // 선택된 탭 (기본값: "전체")
    val isLoadingTags: Boolean = false // 태그 로딩 상태
)