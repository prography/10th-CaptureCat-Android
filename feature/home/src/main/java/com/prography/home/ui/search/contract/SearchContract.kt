package com.prography.home.ui.search.contract

import com.prography.domain.model.UiScreenshotModel

// Define UI State
data class SearchState(
    val searchQuery: String = "",
    val selectedTags: List<String> = emptyList(),
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val popularTags: List<TagWithCount> = emptyList(),
    val relatedTags: List<String> = emptyList(),
    val searchResults: List<UiScreenshotModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val hasSearched: Boolean = false
)

// Define Actions
sealed class SearchAction {
    object LoadScreenshots : SearchAction()
    data class UpdateSearchQuery(val query: String) : SearchAction()
    data class SearchByTag(val tag: String) : SearchAction()
    data class AddTag(val tag: String) : SearchAction()
    data class RemoveTag(val tag: String) : SearchAction()
    object ClearSearch : SearchAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : SearchAction()
    object OnSearchComplete : SearchAction()
    object NavigateToStorage : SearchAction()
}

// Define Effects
sealed class SearchEffect {
    data class ShowError(val message: String) : SearchEffect()
    object NavigateToStorage : SearchEffect()
}

// Helper data class for tags with count
data class TagWithCount(
    val tag: String,
    val count: Int
)