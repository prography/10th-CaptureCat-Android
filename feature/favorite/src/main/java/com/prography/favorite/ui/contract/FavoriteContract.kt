package com.prography.favorite.ui.contract

import com.prography.domain.model.UiScreenshotModel

data class FavoriteState(
    val favoriteScreenshots: List<UiScreenshotModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasData: Boolean = false
)

sealed class FavoriteEffect {
    data class ShowError(val message: String) : FavoriteEffect()
}

sealed class FavoriteAction {
    object LoadFavoriteScreenshots : FavoriteAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : FavoriteAction()
    object OnNavigateUp : FavoriteAction()
}