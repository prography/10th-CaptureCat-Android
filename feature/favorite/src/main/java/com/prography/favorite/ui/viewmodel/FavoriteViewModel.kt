package com.prography.favorite.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.model.TagModel
import com.prography.favorite.ui.contract.FavoriteAction
import com.prography.favorite.ui.contract.FavoriteEffect
import com.prography.favorite.ui.contract.FavoriteState
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.screenshot.GetFavoriteImagesUseCase
import com.prography.domain.usecase.screenshot.GetFavoriteTagsUseCase
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import com.prography.domain.usecase.screenshot.SearchFavoriteImagesByTagUseCase
import com.prography.domain.usecase.screenshot.ToggleBookmarkUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getFavoriteTagsUseCase: GetFavoriteTagsUseCase,
    private val searchFavoriteImagesByTagUseCase: SearchFavoriteImagesByTagUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<FavoriteState, FavoriteEffect, FavoriteAction>(FavoriteState()) {

    init { sendAction(FavoriteAction.LoadFavoriteScreenshots) }

    override fun handleAction(action: FavoriteAction) {
        when (action) {
            FavoriteAction.LoadFavoriteScreenshots -> loadFavoriteScreenshots()
            is FavoriteAction.OnScreenshotClick -> handleScreenshotClick(action.screenshot)
            is FavoriteAction.OnToggleFavorite -> handleToggleFavorite(action.screenshot)
            is FavoriteAction.OnTagSelected -> {
                MixpanelUtil.track("tag_tab_click", mapOf("tab_name" to (action.tag?.name ?: "전체")))
                MixpanelUtil.track("tag_tab_click", mapOf("page_type" to "favorite"))
                applyTagFilter(action.tag)
            }
            FavoriteAction.NavigateToTagSetting -> navigationHelper.navigate(NavigationEvent.To(AppRoute.TagSetting))
            FavoriteAction.OnNavigateUp -> navigationHelper.navigate(NavigationEvent.Up)
        }
    }

    private fun loadFavoriteScreenshots() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            getFavoriteImagesUseCase().onSuccess { favorites ->
                val tags = runCatching { getFavoriteTagsUseCase(10) }.getOrElse { emptyList() }
                val selectedTag = currentState.selectedTag
                val displayed = filterByTag(favorites, selectedTag)

                updateState {
                    copy(
                        favoriteScreenshots = favorites,
                        displayedScreenshots = displayed,
                        popularTags = tags,
                        hasData = favorites.isNotEmpty(),
                        isLoading = false
                    )
                }
            }.onFailure {
                updateState { copy(isLoading = false) }
                showToast("즐겨찾기 목록을 불러오는 중 오류가 발생했습니다.")
            }
        }
    }

    private fun applyTagFilter(tag: TagModel?) {
        viewModelScope.launch {
            updateState { copy(selectedTag = tag, isFiltering = true) }

            val result = if (tag == null) {
                getFavoriteImagesUseCase()
            } else {
                tag.id?.let { searchFavoriteImagesByTagUseCase(tagId = it.toInt()) }
            }

            result?.onSuccess { favorites ->
                updateState {
                    copy(
                        displayedScreenshots = favorites,
                        isFiltering = false
                    )
                }
            }?.onFailure {
                updateState { copy(isFiltering = false) }
                showToast("태그 필터링 중 오류가 발생했습니다.")
            }
        }
    }

    private fun filterByTag(all: List<UiScreenshotModel>, tag: TagModel?): List<UiScreenshotModel> {
        return when {
            tag == null -> all
            else -> all.filter { it.tags.any { t -> t.name == tag.name } }
        }
    }

    private fun handleScreenshotClick(clicked: UiScreenshotModel) {
        val list = currentState.displayedScreenshots
        val idx = list.indexOf(clicked)
        if (idx != -1) {
            navigationHelper.navigate(
                NavigationEvent.To(
                    AppRoute.ImageDetail(
                        screenshotIds = list.map { it.id },
                        currentIndex = idx,
                        entryPoint = "favorite_detail"
                    )
                )
            )
        }
    }

    private fun handleToggleFavorite(screenshot: UiScreenshotModel) {
        viewModelScope.launch {
            try {
                toggleBookmarkUseCase(screenshot.id, false)
                loadFavoriteScreenshots()
            } catch (e: Exception) {
                showToast("즐겨찾기 해제 중 오류가 발생했습니다.")
            }
        }
    }
}

