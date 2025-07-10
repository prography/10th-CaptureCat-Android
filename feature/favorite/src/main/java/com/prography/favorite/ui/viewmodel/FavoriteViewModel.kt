package com.prography.favorite.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.favorite.ui.contract.FavoriteAction
import com.prography.favorite.ui.contract.FavoriteEffect
import com.prography.favorite.ui.contract.FavoriteState
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.screenshot.GetAllBookmarksUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllBookmarksUseCase: GetAllBookmarksUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<FavoriteState, FavoriteEffect, FavoriteAction>(
    initialState = FavoriteState()
) {

    init {
        sendAction(FavoriteAction.LoadFavoriteScreenshots)
    }

    override fun handleAction(action: FavoriteAction) {
        when (action) {
            FavoriteAction.LoadFavoriteScreenshots -> loadFavoriteScreenshots()
            is FavoriteAction.OnScreenshotClick -> handleScreenshotClick(action.screenshot)
            FavoriteAction.OnNavigateUp -> {
                navigationHelper.navigate(NavigationEvent.Up)
            }
        }
    }

    private fun loadFavoriteScreenshots() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }

                getAllBookmarksUseCase()
                    .catch { exception ->
                        updateState { copy(isLoading = false) }
                        emitEffect(FavoriteEffect.ShowError("즐겨찾기 목록을 불러오는 중 오류가 발생했습니다."))
                    }
                    .collect { favoriteScreenshots ->
                        updateState {
                            copy(
                                favoriteScreenshots = favoriteScreenshots,
                                hasData = favoriteScreenshots.isNotEmpty(),
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                emitEffect(FavoriteEffect.ShowError("즐겨찾기 목록을 불러오는 중 오류가 발생했습니다."))
            }
        }
    }

    private fun handleScreenshotClick(clickedScreenshot: UiScreenshotModel) {
        val currentFavorites = currentState.favoriteScreenshots
        val currentIndex = currentFavorites.indexOf(clickedScreenshot)

        if (currentIndex != -1) {
            val screenshotIds = currentFavorites.map { it.id }
            navigationHelper.navigate(
                NavigationEvent.To(
                    AppRoute.ImageDetail(
                        screenshotIds = screenshotIds,
                        currentIndex = currentIndex
                    )
                )
            )
        }
    }
}