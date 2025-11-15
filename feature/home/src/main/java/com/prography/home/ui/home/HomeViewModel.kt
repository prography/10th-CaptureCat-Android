package com.prography.home.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.auth.CheckLoginStatusUseCase
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import com.prography.domain.usecase.screenshot.SearchImagesByTagsUseCase
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.user.GetDeleteChoiceSettingUseCase
import com.prography.domain.usecase.user.GetDeletePromptSettingUseCase
import com.prography.domain.usecase.user.SetDeleteChoiceSettingUseCase
import com.prography.domain.usecase.user.SetDeletePromptSettingUseCase
import com.prography.home.ui.home.component.GetImageUrisByIdsUseCase
import com.prography.home.ui.home.component.ScreenshotPagingSource
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.home.ui.home.contract.HomeState
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.navigation.StorageMode
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScreenshotsUseCase: GetAllScreenshotsUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val getMostUsedTagsUseCase: GetMostUsedTagsUseCase,
    private val searchImagesByTagsUseCase: SearchImagesByTagsUseCase,
    private val navigationHelper: NavigationHelper,
    private val getDeletePromptSettingUseCase: GetDeletePromptSettingUseCase,
    private val getDeleteChoiceSettingUseCase: GetDeleteChoiceSettingUseCase,
    private val setDeletePromptSettingUseCase: SetDeletePromptSettingUseCase,
    private val setDeleteChoiceSettingUseCase: SetDeleteChoiceSettingUseCase,
    private val getImageUrisByIdsUseCase: GetImageUrisByIdsUseCase
) : BaseComposeViewModel<HomeState, HomeEffect, HomeAction>(HomeState()) {

    private var hasCheckedLoginStatus = false

    val screenshotsPagingFlow: Flow<PagingData<UiScreenshotModel>> =
        Pager(
            config = PagingConfig(pageSize = 20,
                initialLoadSize = 20,enablePlaceholders = false),
            pagingSourceFactory = {
                ScreenshotPagingSource(getScreenshotsUseCase)
            }
        ).flow.cachedIn(viewModelScope)

    fun checkLoginStatusOnFirstAccess() {
        if (!hasCheckedLoginStatus) {
            hasCheckedLoginStatus = true
            val isLoggedIn = checkLoginStatusUseCase()
            if (!isLoggedIn) {
                // 게스트 모드라면 로그인 다이얼로그 표시
                updateState { copy(showLoginDialog = true) }
            }
        }
    }

    fun loadMostUsedTags() {
        viewModelScope.launch {
            try {
                val tags = getMostUsedTagsUseCase(size = 20)
                updateState { copy(popularTags = tags) }
            } catch (exception: Exception) {
                Timber.e(exception, "Failed to load most used tags")
                emitEffect(HomeEffect.ShowError("인기 태그를 불러오는 중 오류가 발생했습니다."))
            }
        }
    }

    fun searchImagesByTag(tagName: String) {
        viewModelScope.launch {
            try {
                updateState { copy(selectedTab = tagName, isLoadingTags = true) }
                val images = if (tagName == "전체") {
                    // 전체인 경우 기본 스크린샷 사용 (paging으로 처리됨)
                    emptyList()
                } else {
                    searchImagesByTagsUseCase(listOf(tagName), page = 0, size = 50)
                }
                updateState {
                    copy(
                        screenshots = images,
                        selectedTab = tagName,
                        isLoadingTags = false
                    )
                }
            } catch (exception: Exception) {
                Timber.e(exception, "Failed to search images by tag: $tagName")
                updateState { copy(isLoadingTags = false) }
                emitEffect(HomeEffect.ShowError("태그 검색 중 오류가 발생했습니다."))
            }
        }
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.NavigateToFavorite -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Favorite)
                )
            }
            HomeAction.NavigateToStorageUpload -> {
                MixpanelUtil.track("image_fab_click", mapOf("page_type" to "캡처 업로드"))
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Storage(mode = StorageMode.Upload))
                )
            }
            HomeAction.NavigateToStorageOrganize -> {
                MixpanelUtil.track("image_fab_click", mapOf("page_type" to "캡처 정리"))
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Storage(mode = StorageMode.Organize))
                )
            }
            HomeAction.NavigateToMyPage -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.MyPage)
                )
            }
            is HomeAction.OnScreenshotClick -> {
                handleScreenshotClick(action.screenshot)
            }
            HomeAction.ShowLoginDialog -> {
                updateState { copy(showLoginDialog = true) }
            }

            HomeAction.HideLoginDialog -> {
                updateState { copy(showLoginDialog = false) }
            }

            HomeAction.NavigateToLogin -> {
                updateState { copy(showLoginDialog = false) }
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Login)
                )
            }
            HomeAction.OnErrorReportClick -> {
                handleErrorReportClick()
            }

            HomeAction.DismissErrorReportBanner -> {
                dismissErrorReportBanner()
            }
            is HomeAction.OnTabSelected -> {
                MixpanelUtil.track("tag_tab_click", mapOf("tab_name" to action.tabTag))
                MixpanelUtil.track("tag_tab_click", mapOf("page_type" to "home"))
                searchImagesByTag(action.tabTag)
            }
            HomeAction.NavigateToTagSetting -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.TagSetting)
                )
            }
            is HomeAction.OnArriveWithIds -> handleArriveWithIds(action.ids)

            is HomeAction.OnDeleteChoiceConfirm -> {
                // 버튼 누르면: 선택값 저장 + 즉시 시스템 알럿
                viewModelScope.launch {
                    runCatching { setDeleteChoiceSettingUseCase(true) }
                    requestSystemDeleteByIds(action.ids)
                }
            }

            HomeAction.OnDeleteChoiceLater -> {
                // 버튼 누르면: 선택값 저장 + 아무 것도 안 함(바텀싯만 닫기)
                viewModelScope.launch {
                    runCatching {
                        setDeleteChoiceSettingUseCase(true)
                        setDeletePromptSettingUseCase(false)
                    }
                }
            }

            is HomeAction.OnSystemDeleteResult -> {
                // 시스템 알럿 결과(성공 개수) 처리
                val n = action.successCount
                if (n > 0) showToast("${n}장 삭제되었어요.")
            }
        }
    }

    private fun handleArriveWithIds(ids: List<String>) {
        if (ids.isEmpty()) return

        viewModelScope.launch {
            val hasChosen = getDeleteChoiceSettingUseCase().getOrElse { false }
            val promptEnabled = getDeletePromptSettingUseCase().getOrElse { false }

            if (!hasChosen) {
                // 1) 아직 선택한 적이 없다면: 바텀싯 표시
                emitEffect(HomeEffect.ShowDeleteChoiceBottomSheet(ids))
                return@launch
            }

            if (promptEnabled) {
                // 2) 이미 선택했고 프롬프트도 허용 → 즉시 시스템 알럿
                requestSystemDeleteByIds(ids)
            }
            // 3) hasChosen = true 이지만 promptEnabled = false 이면 아무 것도 안함
        }
    }

    private suspend fun requestSystemDeleteByIds(ids: List<String>) {
        val uris = runCatching { getImageUrisByIdsUseCase(ids) }.getOrDefault(emptyList())
        if (uris.isEmpty()) return
        emitEffect(HomeEffect.RequestSystemDelete(uris))
    }

    private fun handleScreenshotClick(clickedScreenshot: UiScreenshotModel) {
        // Paging 3에서는 전체 리스트를 가져오는 방식이 다름
        // 현재 로드된 페이지의 아이템들만 사용하거나, 별도 로직 필요
        navigationHelper.navigate(
            NavigationEvent.To(
                AppRoute.ImageDetail(
                    screenshotIds = listOf(clickedScreenshot.id), // 우선 현재 아이템만
                    currentIndex = 0,
                    entryPoint = "home_detail"
                )
            )
        )
    }

    private fun handleErrorReportClick() {
        // 채팅으로 오류 제보 - 카카오톡 채널 링크
        emitEffect(HomeEffect.OpenExternalLink("https://pf.kakao.com/_AKjvn"))
    }

    private fun dismissErrorReportBanner() {
        updateState { copy(showErrorReportBanner = false) }
    }
}