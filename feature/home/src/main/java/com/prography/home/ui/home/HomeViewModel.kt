package com.prography.home.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.auth.CheckLoginStatusUseCase
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetFavoriteImagesUseCase
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.home.component.ScreenshotPagingSource
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeEffect
import com.prography.home.ui.home.contract.HomeState
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScreenshotsUseCase: GetAllScreenshotsUseCase,
    private val getFavoriteImagesUseCase: GetFavoriteImagesUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val navigationHelper: NavigationHelper
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

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.NavigateToFavorite -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Favorite)
                )
            }
            HomeAction.NavigateToStorage -> {
                emitEffect(HomeEffect.NavigateToStorage)
            }
            HomeAction.NavigateToSearch -> {
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Search)
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
        }
    }

    fun loadFavoriteImages() {
        viewModelScope.launch {
            try {
                getFavoriteImagesUseCase(page = 0, size = 10).fold(
                    onSuccess = { favoriteImages ->
                        Timber.d("HomeViewModel - Favorite images loaded: ${favoriteImages.size} items")
                        updateState {
                            copy(favoriteScreenshots = favoriteImages)
                        }
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "HomeViewModel - Failed to load favorite images")
                        emitEffect(HomeEffect.ShowError("Failed to load favorite images: ${exception.message}"))
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "HomeViewModel - Exception while loading favorite images")
                emitEffect(HomeEffect.ShowError("Failed to load favorite images: ${e.message}"))
            }
        }
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
        // 채팅으로 오류 제보 - 외부 링크나 채팅앱으로 연결
        // TODO: 실제 채팅 서비스 연결 (예: 카카오톡 채널, 구글 폼 등)
        emitEffect(HomeEffect.OpenErrorReportChat)
    }

    private fun dismissErrorReportBanner() {
        updateState { copy(showErrorReportBanner = false) }
    }
}