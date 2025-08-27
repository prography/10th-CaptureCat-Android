package com.prography.home.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import com.prography.domain.usecase.screenshot.SearchImagesByTagsUseCase
import com.prography.domain.usecase.screenshot.GetRelatedTagsUseCase
import com.prography.domain.usecase.screenshot.GetUncategorizedScreenshotsUseCase
import com.prography.domain.model.TagWithCount
import com.prography.home.ui.search.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getMostUsedTagsUseCase: GetMostUsedTagsUseCase,
    private val searchImagesByTagsUseCase: SearchImagesByTagsUseCase,
    private val getRelatedTagsUseCase: GetRelatedTagsUseCase,
    private val getUncategorizedScreenshotsUseCase: GetUncategorizedScreenshotsUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<SearchState, SearchEffect, SearchAction>(SearchState()) {

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
                                            TagWithCount(
                                                "미분류",
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
                    val screenshots = currentState.screenshots
                    if (screenshots.isNotEmpty()) {
                        val popularTags = getPopularTags(screenshots)
                        updateState { copy(popularTags = popularTags) }
                    }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        updateState {
            copy(
                searchQuery = query,
                hasSearched = if (query.isEmpty()) false else hasSearched
            )
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
            updateState {
                copy(
                    selectedTags = newTags,
                    searchQuery = ""
                )
            }

            // 미분류 태그인 경우 특별 처리
            if (tag == "미분류") {
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
            updateState {
                copy(
                    searchResults = emptyList(),
                    relatedTags = emptyList()
                )
            }
        } else {
            // 미분류가 아닌 태그들만 있는 경우 일반 검색
            if (!newTags.contains("미분류")) {
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

        viewModelScope.launch {
            runCatching { searchImagesByTagsUseCase(selectedTags) }
                .onSuccess { results ->
                    updateState { copy(searchResults = results) }
                }
                .onFailure {
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
        updateState {
            copy(
                searchQuery = "",
                selectedTags = emptyList(),
                searchResults = emptyList(),
                relatedTags = emptyList(),
                hasSearched = false
            )
        }
    }

    private fun handleSearchComplete() {
        val query = currentState.searchQuery.trim()
        if (query.isEmpty()) return

        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 먼저 검색해서 결과가 있는지 확인
                val searchResults = searchImagesByTagsUseCase(listOf(query))

                if (searchResults.isNotEmpty()) {
                    // 결과가 있으면 태그를 selectedTags에 추가
                    val newTags = listOf(query) + currentState.selectedTags
                    updateState {
                        copy(
                            selectedTags = newTags,
                            searchResults = searchResults,
                            hasSearched = true,
                            isLoading = false
                        )
                    }

                    // 연관 태그 업데이트
                    updateRelatedTags(newTags)
                } else {
                    // 결과가 없으면 에러 상태로 설정
                    updateState {
                        copy(
                            searchQuery = "",
                            searchResults = emptyList(),
                            hasSearched = true,
                            isLoading = false
                        )
                    }
                    emitEffect(SearchEffect.ShowError("'$query' 태그에 해당하는 스크린샷이 없습니다."))
                }
            } catch (exception: Exception) {
                updateState {
                    copy(
                        searchQuery = "",
                        isLoading = false
                    )
                }
                emitEffect(SearchEffect.ShowError("검색 중 오류가 발생했습니다."))
            }
        }
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

    private fun getPopularTags(screenshots: List<com.prography.domain.model.UiScreenshotModel>): List<TagWithCount> {
        val tagCounts = mutableMapOf<String, Int>()

        screenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                tagCounts[tag.name] = tagCounts.getOrDefault(tag.name, 0) + 1
            }
        }

        val popularTags = tagCounts.entries
            .map { TagWithCount(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(5) // 상위 5개 태그만
            .plus(TagWithCount("미분류", screenshots.count { it.tags.isEmpty() })) // 미분류 태그 추가
        return popularTags
    }

    private fun navigateToStorage() {
        emitEffect(SearchEffect.NavigateToStorage)
    }

    private fun handleRefreshSearchResults() {
        val selectedTags = currentState.selectedTags
        if (selectedTags.isNotEmpty()) {
            timber.log.Timber.d("🔄 Refreshing search results with tags: $selectedTags")
            // 미분류 태그인 경우 특별 처리
            if (selectedTags.contains("미분류")) {
                searchUncategorizedScreenshots()
            } else {
                searchBySelectedTags(selectedTags)
                updateRelatedTags(selectedTags)
            }
        }
    }
}