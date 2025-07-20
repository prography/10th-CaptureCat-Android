package com.prography.home.ui.home

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetFavoriteImagesUseCase
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.home.ui.home.contract.HomeState
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScreenshotsUseCase: GetAllScreenshotsUseCase,
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<HomeState, HomeEffect, HomeAction>(HomeState()) {

    init {
        loadScreenshots()
        loadFavoriteImages()
    }

    fun refreshScreenshots() {
        loadScreenshots()
        loadFavoriteImages()
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadScreenshots -> loadScreenshots()
            is HomeAction.SelectTag -> selectTag(action.tag)
            HomeAction.NavigateToSettings -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.SettingRoute.Setting)
                )
            }
            HomeAction.NavigateToFavorite -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Favorite)
                )
            }
            HomeAction.NavigateToStorage -> {
                // 임시보관함으로 이동하는 로직
                // Storage 탭으로 이동하려면 HomeScreen에서 콜백을 받아서 처리해야 함
                // 여기서는 effect로 전달
                emitEffect(HomeEffect.NavigateToStorage)
            }
            is HomeAction.OnScreenshotClick -> {
                handleScreenshotClick(action.screenshot)
            }
        }
    }

    private fun loadScreenshots() {
        viewModelScope.launch {
            try {
                getScreenshotsUseCase().collect { screenshots ->
                    updateState {
                        copy(screenshots = screenshots)
                    }
                }
            } catch (e: Exception) {
                emitEffect(HomeEffect.ShowError("Failed to load screenshots: ${e.message}"))
            }
        }
    }

    private fun loadFavoriteImages() {
        viewModelScope.launch {
            try {
                getFavoriteImagesUseCase(page = 0, size = 10).fold(
                    onSuccess = { favoriteImages ->
                        timber.log.Timber.d("HomeViewModel - Favorite images loaded: ${favoriteImages.size} items")
                        favoriteImages.forEach { image ->
                            timber.log.Timber.d("HomeViewModel - Favorite image: id=${image.id}, isBookmarked=${image.isBookmarked}")
                        }
                        updateState {
                            copy(favoriteScreenshots = favoriteImages)
                        }
                        timber.log.Timber.d("HomeViewModel - State updated with ${favoriteImages.size} favorite screenshots")
                    },
                    onFailure = { exception ->
                        timber.log.Timber.e(
                            exception,
                            "HomeViewModel - Failed to load favorite images"
                        )
                        emitEffect(HomeEffect.ShowError("Failed to load favorite images: ${exception.message}"))
                    }
                )
            } catch (e: Exception) {
                timber.log.Timber.e(e, "HomeViewModel - Exception while loading favorite images")
                emitEffect(HomeEffect.ShowError("Failed to load favorite images: ${e.message}"))
            }
        }
    }

    private fun selectTag(tag: String) {
        updateState {
            copy(selectedTag = tag)
        }
    }

    private fun handleScreenshotClick(clickedScreenshot: UiScreenshotModel) {
        val currentState = currentState
        val filteredScreenshots = currentState.screenshots

        val currentIndex = filteredScreenshots.indexOf(clickedScreenshot)
        if (currentIndex != -1) {
            navigationHelper.navigate(NavigationEvent.To(AppRoute.ImageDetail(screenshotIds = filteredScreenshots.map { it.id }, currentIndex = currentIndex)))
        }
    }
}