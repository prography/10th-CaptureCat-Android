package com.prography.home.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.SearchImagesByTagsUseCase
import com.prography.domain.usecase.screenshot.GetRelatedTagsUseCase
import com.prography.domain.usecase.screenshot.GetUncategorizedScreenshotsUseCase
import com.prography.home.ui.search.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchImagesByTagsUseCase: SearchImagesByTagsUseCase,
    private val getRelatedTagsUseCase: GetRelatedTagsUseCase,
    private val getUncategorizedScreenshotsUseCase: GetUncategorizedScreenshotsUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<SearchState, SearchEffect, SearchAction>(SearchState()) {

    fun loadSearchResults(selectedTags: List<String>) {
        if (selectedTags.isEmpty()) return

        Timber.d("🔍 SearchResultsViewModel: Loading results for tags: $selectedTags")

        updateState {
            copy(
                selectedTags = selectedTags,
                hasSearched = true,
                isLoading = true
            )
        }

        // 미분류 태그인 경우 특별 처리
        if (selectedTags.contains("태그 없음")) {
            searchUncategorizedScreenshots()
        } else {
            searchBySelectedTags(selectedTags)
            updateRelatedTags(selectedTags)
        }
    }

    override fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.AddTag -> addTag(action.tag)
            is SearchAction.RemoveTag -> removeTag(action.tag)
            is SearchAction.ClearSearch -> clearSearch()
            is SearchAction.OnScreenshotClick -> handleScreenshotClick(action.screenshot)
            is SearchAction.NavigateToStorage -> navigateToStorage()
            is SearchAction.RefreshSearchResults -> handleRefreshSearchResults()
            is SearchAction.NavigateBackToSearch -> navigateBackToSearch()
            else -> {
                // 다른 액션들은 SearchResultsScreen에서 처리하지 않음
                Timber.w("🔍 SearchResultsViewModel: Unhandled action: $action")
            }
        }
    }

    private fun addTag(tag: String) {
        val currentTags = currentState.selectedTags
        if (!currentTags.contains(tag)) {
            val newTags = listOf(tag) + currentTags
            Timber.d("🔍 SearchResultsViewModel: Adding tag: $tag, newTags: $newTags")

            updateState {
                copy(selectedTags = newTags)
            }

            // 미분류 태그인 경우 특별 처리
            if (tag == "태그 없음") {
                searchUncategorizedScreenshots()
            } else {
                searchBySelectedTags(newTags)
                updateRelatedTags(newTags)
            }
        }
    }

    private fun removeTag(tag: String) {
        val newTags = currentState.selectedTags - tag
        updateState { copy(selectedTags = newTags) }

        if (newTags.isEmpty()) {
            // 모든 태그가 제거되면 검색 화면으로 돌아감
            emitEffect(SearchEffect.NavigateBackToSearch)
        } else {
            // 미분류가 아닌 태그들만 있는 경우 일반 검색
            if (!newTags.contains("태그 없음")) {
                searchBySelectedTags(newTags)
                updateRelatedTags(newTags)
            } else {
                // 미분류가 포함된 경우 미분류 검색
                searchUncategorizedScreenshots()
            }
        }
    }

    private fun searchBySelectedTags(selectedTags: List<String>) {
        if (selectedTags.isEmpty()) {
            updateState { copy(searchResults = emptyList(), isLoading = false) }
            return
        }

        Timber.d("🔍 SearchResultsViewModel: Starting search with tags: $selectedTags")
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            runCatching { searchImagesByTagsUseCase(selectedTags) }
                .onSuccess { results ->
                    Timber.d("🔍 SearchResultsViewModel: Search completed: ${results.size} results found")
                    updateState {
                        copy(
                            searchResults = results,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    Timber.e(exception, "🔍 SearchResultsViewModel: Search failed")
                    updateState { copy(isLoading = false) }
                    emitEffect(SearchEffect.ShowError("스크린샷을 불러오는 중 오류가 발생했습니다."))
                }
        }
    }

    private fun searchUncategorizedScreenshots() {
        Timber.d("🔍 SearchResultsViewModel: Searching uncategorized screenshots")
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            runCatching { getUncategorizedScreenshotsUseCase() }
                .onSuccess { screenshotsFlow ->
                    screenshotsFlow.collect { results ->
                        Timber.d("🔍 SearchResultsViewModel: Uncategorized search completed: ${results.size} results found")
                        updateState {
                            copy(
                                searchResults = results,
                                relatedTags = emptyList(), // 미분류는 연관 태그 없음
                                isLoading = false
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    Timber.e(exception, "🔍 SearchResultsViewModel: Uncategorized search failed")
                    updateState { copy(isLoading = false) }
                    emitEffect(SearchEffect.ShowError("미분류 스크린샷을 불러오는 중 오류가 발생했습니다."))
                }
        }
    }

    private fun updateRelatedTags(selectedTags: List<String>) {
        if (selectedTags.isEmpty()) {
            updateState { copy(relatedTags = emptyList()) }
            return
        }

        viewModelScope.launch {
            runCatching { getRelatedTagsUseCase(selectedTags) }
                .onSuccess { relatedTags ->
                    Timber.d("🔍 SearchResultsViewModel: Related tags loaded: $relatedTags")
                    updateState { copy(relatedTags = relatedTags) }
                }
                .onFailure { exception ->
                    Timber.e(exception, "🔍 SearchResultsViewModel: Failed to load related tags")
                    emitEffect(SearchEffect.ShowError("연관 태그를 불러오는 중 오류가 발생했습니다."))
                }
        }
    }

    private fun clearSearch() {
        Timber.d("🔍 SearchResultsViewModel: Clearing search")
        updateState {
            copy(
                selectedTags = emptyList(),
                searchResults = emptyList(),
                relatedTags = emptyList(),
                hasSearched = false,
                isLoading = false
            )
        }
        emitEffect(SearchEffect.NavigateBackToSearch)
    }

    private fun handleScreenshotClick(clickedScreenshot: com.prography.domain.model.UiScreenshotModel) {
        val currentResults = currentState.searchResults
        val currentIndex = currentResults.indexOf(clickedScreenshot)

        if (currentIndex != -1) {
            navigationHelper.navigate(
                NavigationEvent.To(
                    AppRoute.ImageDetail(
                        screenshotIds = currentResults.map { it.id },
                        currentIndex = currentIndex,
                        entryPoint = "search_results"
                    )
                )
            )
        }
    }

    private fun navigateToStorage() {
        emitEffect(SearchEffect.NavigateToStorage)
    }

    private fun navigateBackToSearch() {
        emitEffect(SearchEffect.NavigateBackToSearch)
    }

    private fun handleRefreshSearchResults() {
        val selectedTags = currentState.selectedTags
        if (selectedTags.isNotEmpty()) {
            Timber.d("🔄 SearchResultsViewModel: Refreshing search results with tags: $selectedTags")
            if (selectedTags.contains("태그 없음")) {
                searchUncategorizedScreenshots()
            } else {
                searchBySelectedTags(selectedTags)
                updateRelatedTags(selectedTags)
            }
        }
    }
}