package com.prography.home.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import com.prography.domain.usecase.screenshot.SearchImagesByTagsUseCase
import com.prography.domain.usecase.screenshot.GetRelatedTagsUseCase
import com.prography.domain.usecase.screenshot.GetUncategorizedScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetSearchAutoCompleteUseCase
import com.prography.domain.model.TagWithCount
import com.prography.home.ui.search.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getMostUsedTagsUseCase: GetMostUsedTagsUseCase,
    private val searchImagesByTagsUseCase: SearchImagesByTagsUseCase,
    private val getRelatedTagsUseCase: GetRelatedTagsUseCase,
    private val getUncategorizedScreenshotsUseCase: GetUncategorizedScreenshotsUseCase,
    private val getSearchAutoCompleteUseCase: GetSearchAutoCompleteUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<SearchState, SearchEffect, SearchAction>(SearchState()) {

    private var autocompleteJob: Job? = null

    init {
        loadMostUsedTags()
    }

    override fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateSearchQuery -> updateSearchQuery(action.query)
            is SearchAction.SearchByTag -> searchByTag(action.tag)
            is SearchAction.AddTag -> addTag(action.tag)
            is SearchAction.RemoveTag -> removeTag(action.tag)
            is SearchAction.ClearSearch -> clearSearch()
            is SearchAction.OnScreenshotClick -> handleScreenshotClick(action.screenshot)
            is SearchAction.OnSearchComplete -> handleSearchComplete()
            is SearchAction.NavigateToStorage -> navigateToStorage()
            is SearchAction.RefreshSearchResults -> handleRefreshSearchResults()
            is SearchAction.NavigateToSearchResults -> navigateToSearchResults()
            is SearchAction.HideAutocomplete -> hideAutocomplete()
            is SearchAction.NavigateBackToSearch -> navigateBackToSearch()
        }
    }

    fun loadMostUsedTags() {
        viewModelScope.launch {
            runCatching { getMostUsedTagsUseCase(size = 20) }
                .onSuccess { topTags ->
                    val tagsWithMiscategorized = topTags.toMutableList()

                    // 미분류 스크린샷이 있는지 확인
                    if (tagsWithMiscategorized.isNotEmpty()) {
                        runCatching { getUncategorizedScreenshotsUseCase() }
                            .onSuccess { uncategorizedFlow ->
                                uncategorizedFlow.collect { uncategorizedScreenshots ->
                                    if (uncategorizedScreenshots.isNotEmpty()) {
                                        tagsWithMiscategorized.add(
                                            0,
                                            TagWithCount(
                                                0,
                                                "태그 없음",
                                                uncategorizedScreenshots.size
                                            )
                                        )
                                    }
                                    updateState { copy(popularTags = tagsWithMiscategorized) }
                                }
                            }
                            .onFailure {
                                updateState { copy(popularTags = tagsWithMiscategorized) }
                            }
                    } else {
                        updateState { copy(popularTags = tagsWithMiscategorized) }
                    }
                }
                .onFailure {
                    showToast("오류가 발생했습니다.")
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        updateState {
            copy(
                searchQuery = query,
                showAutocomplete = query.isNotEmpty(),
                hasSearched = if (query.isEmpty()) false else hasSearched
            )
        }

        // Cancel previous autocomplete job
        autocompleteJob?.cancel()

        if (query.isNotEmpty()) {
            // Start new autocomplete search with delay
            autocompleteJob = viewModelScope.launch {
                delay(500) // 500ms delay to avoid too many API calls
                runCatching { getSearchAutoCompleteUseCase(query) }
                    .onSuccess { autocompleteResults ->
                        updateState {
                            copy(
                                autocompleteResults = autocompleteResults,
                                showAutocomplete = true
                            )
                        }
                    }
                    .onFailure {
                        updateState {
                            copy(
                                autocompleteResults = emptyList(),
                                showAutocomplete = false
                            )
                        }
                    }
            }
        } else {
            // Clear autocomplete when query is empty
            updateState {
                copy(
                    autocompleteResults = emptyList(),
                    showAutocomplete = false
                )
            }
        }
    }

    private fun searchByTag(tag: String) {
        updateState { copy(searchQuery = tag) }
        searchScreenshots(tag)
    }

    private fun searchScreenshots(query: String) {
        val results = currentState.screenshots.filter { screenshot ->
            screenshot.tags.any { tag ->
                tag.name.contains(query, ignoreCase = true)
            }
        }

        updateState { copy(searchResults = results) }
    }

    private fun addTag(tag: String) {
        val currentTags = currentState.selectedTags
        if (!currentTags.contains(tag)) {
            val newTags = listOf(tag) + currentTags
            timber.log.Timber.d("🔍 Adding tag: $tag, newTags: $newTags")

            updateState {
                copy(
                    selectedTags = newTags,
                    searchQuery = "",
                    showAutocomplete = false,
                    autocompleteResults = emptyList(),
                    hasSearched = true
                )
            }

            timber.log.Timber.d("🔍 Navigating to search results")
            emitEffect(SearchEffect.NavigateToSearchResults)
        }
    }

    private fun removeTag(tag: String) {
        val newTags = currentState.selectedTags - tag
        updateState { copy(selectedTags = newTags) }

        if (newTags.isEmpty()) {
            updateState {
                copy(
                    searchResults = emptyList(),
                    relatedTags = emptyList()
                )
            }
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
            updateState { copy(searchResults = emptyList()) }
            return
        }

        timber.log.Timber.d("🔍 Starting search with tags: $selectedTags")
        viewModelScope.launch {
            runCatching { searchImagesByTagsUseCase(selectedTags) }
                .onSuccess { results ->
                    timber.log.Timber.d("🔍 Search completed: ${results.size} results found")
                    updateState { copy(searchResults = results) }
                }
                .onFailure { exception ->
                    timber.log.Timber.e(exception, "🔍 Search failed")
                    emitEffect(SearchEffect.ShowError("스크린샷을 불러오는 중 오류가 발생했습니다."))
                }
        }
    }

    private fun searchUncategorizedScreenshots() {
        viewModelScope.launch {
            runCatching { getUncategorizedScreenshotsUseCase() }
                .onSuccess { screenshotsFlow ->
                    screenshotsFlow.collect { results ->
                        updateState {
                            copy(
                                searchResults = results,
                                relatedTags = emptyList() // 미분류는 연관 태그 없음
                            )
                        }
                    }
                }
                .onFailure {
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
                    updateState { copy(relatedTags = relatedTags) }
                }
                .onFailure {
                    emitEffect(SearchEffect.ShowError("연관 태그를 불러오는 중 오류가 발생했습니다."))
                }
        }
    }

    private fun clearSearch() {
        // Cancel any ongoing autocomplete job
        autocompleteJob?.cancel()
        autocompleteJob = null

        updateState {
            copy(
                searchQuery = "",
                selectedTags = emptyList(),
                searchResults = emptyList(),
                relatedTags = emptyList(),
                hasSearched = false,
                showAutocomplete = false,
                autocompleteResults = emptyList(),
                isLoading = false
            )
        }
        // No need to emit NavigateBackToSearch effect as SearchScreen handles view switching
    }

    private fun handleSearchComplete() {
        val query = currentState.searchQuery.trim()
        if (query.isEmpty()) return

        updateState {
            copy(
                selectedTags = listOf(query),
                searchQuery = "",
                showAutocomplete = false,
                autocompleteResults = emptyList(),
                hasSearched = true,
                isLoading = false
            )
        }

        // 검색 결과 화면으로 이동
        emitEffect(SearchEffect.NavigateToSearchResults)
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
                        entryPoint = "search_keyword"
                    )
                )
            )
        }
    }

    private fun navigateToStorage() {
        emitEffect(SearchEffect.NavigateToStorage)
    }

    private fun navigateToSearchResults() {
        emitEffect(SearchEffect.NavigateToSearchResults)
    }

    private fun navigateBackToSearch() {
        emitEffect(SearchEffect.NavigateBackToSearch)
    }

    private fun handleRefreshSearchResults() {
        val selectedTags = currentState.selectedTags
        if (selectedTags.isNotEmpty()) {
            timber.log.Timber.d("🔄 Refreshing search results with tags: $selectedTags")
            // 미분류 태그인 경우 특별 처리
            if (selectedTags.contains("태그 없음")) {
                searchUncategorizedScreenshots()
            } else {
                searchBySelectedTags(selectedTags)
                updateRelatedTags(selectedTags)
            }
        }
    }

    private fun hideAutocomplete() {
        updateState { copy(autocompleteResults = emptyList(), showAutocomplete = false) }
    }
}