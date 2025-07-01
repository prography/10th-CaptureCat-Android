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
        }
    }

    private fun loadScreenshots() {
        viewModelScope.launch {
            try {
                getScreenshotsUseCase().collect { screenshots ->
                    val tags = listOf("전체") + screenshots.map { it.appName }.distinct()
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
}