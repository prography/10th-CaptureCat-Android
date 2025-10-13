package com.prography.favorite.ui.contract

import com.prography.domain.model.TagModel
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel

data class FavoriteState(
    val favoriteScreenshots: List<UiScreenshotModel> = emptyList(),
    val displayedScreenshots: List<UiScreenshotModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasData: Boolean = false,

    val popularTags: List<TagWithCount> = emptyList(),
    val selectedTag: TagModel? = null,
    val isFiltering: Boolean = false
)
sealed class FavoriteEffect {
    data class ShowError(val message: String) : FavoriteEffect()
}

sealed class FavoriteAction {
    object LoadFavoriteScreenshots : FavoriteAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : FavoriteAction()
    data class OnToggleFavorite(val screenshot: UiScreenshotModel) : FavoriteAction()
    object OnNavigateUp : FavoriteAction()

    data class OnTagSelected(val tag: TagModel?) : FavoriteAction()
    object NavigateToTagSetting : FavoriteAction()
}