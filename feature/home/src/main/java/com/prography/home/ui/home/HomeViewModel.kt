package com.prography.home.ui.home

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
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
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<HomeState, HomeEffect, HomeAction>(HomeState()) {

    init {
        sendAction(HomeAction.LoadScreenshots)
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
                    val tags =
                        listOf("전체") + screenshots
                            .map { it.appName }
                            .filter { it.isNotBlank() }
                            .distinct()
                    updateState {
                        copy(screenshots = screenshots, tags = tags)
                    }
                }
            } catch (e: Exception) {
                emitEffect(HomeEffect.ShowError("Failed to load screenshots: ${e.message}"))
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
        val filteredScreenshots = if (currentState.selectedTag == "전체") {
            currentState.screenshots
        } else {
            currentState.screenshots.filter { it.appName == currentState.selectedTag }
        }

        val currentIndex = filteredScreenshots.indexOf(clickedScreenshot)
        if (currentIndex != -1) {
            navigationHelper.navigate(NavigationEvent.To(AppRoute.ImageDetail(screenshotIds = filteredScreenshots.map { it.id }, currentIndex = currentIndex)))
        }
    }
}