package com.prography.home.ui.search.contract

import com.prography.domain.model.AutocompleteTagModel
import com.prography.domain.model.TagModel
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.model.TagWithCount

// Define UI State
data class SearchState(
    val searchQuery: String = "",
    val selectedTags: List<String> = emptyList(),
    val screenshots: List<UiScreenshotModel> = emptyList(),
    val popularTags: List<TagWithCount> = emptyList(),
    val relatedTags: List<String> = emptyList(),
    val searchResults: List<UiScreenshotModel> = emptyList(),
    val autocompleteResults: List<TagModel> = emptyList(),
    val showAutocomplete: Boolean = false,
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false
)

// Define Actions
sealed class SearchAction {
    data class UpdateSearchQuery(val query: String) : SearchAction()
    data class SearchByTag(val tag: String) : SearchAction()
    data class AddTag(val tag: String) : SearchAction()
    data class RemoveTag(val tag: String) : SearchAction()
    object ClearSearch : SearchAction()
    data class OnScreenshotClick(val screenshot: UiScreenshotModel) : SearchAction()
    object OnSearchComplete : SearchAction()
    object NavigateToStorage : SearchAction()
    object RefreshSearchResults : SearchAction()
    object NavigateToSearchResults : SearchAction()
    object HideAutocomplete : SearchAction()
    object NavigateBackToSearch : SearchAction()
}

// Define Effects
sealed class SearchEffect {
    data class ShowError(val message: String) : SearchEffect()
    object NavigateToStorage : SearchEffect()
    object NavigateToSearchResults : SearchEffect()
    object NavigateBackToSearch : SearchEffect()
}
