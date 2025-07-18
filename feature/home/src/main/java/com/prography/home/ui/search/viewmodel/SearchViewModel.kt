package com.prography.home.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.home.ui.search.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllScreenshotsUseCase: GetAllScreenshotsUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<SearchState, SearchEffect, SearchAction>(SearchState()) {

    init {
        sendAction(SearchAction.LoadScreenshots)
    }

    override fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.LoadScreenshots -> loadScreenshots()
            is SearchAction.UpdateSearchQuery -> updateSearchQuery(action.query)
            is SearchAction.SearchByTag -> searchByTag(action.tag)
            is SearchAction.AddTag -> addTag(action.tag)
            is SearchAction.RemoveTag -> removeTag(action.tag)
            is SearchAction.ClearSearch -> clearSearch()
            is SearchAction.OnScreenshotClick -> handleScreenshotClick(action.screenshot)
            is SearchAction.OnSearchComplete -> handleSearchComplete()
            is SearchAction.NavigateToStorage -> navigateToStorage()
        }
    }

    private fun loadScreenshots() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }

                getAllScreenshotsUseCase().collect { screenshots ->
                    val popularTags = getPopularTags(screenshots)
                    val hasData = screenshots.isNotEmpty()

                    updateState {
                        copy(
                            screenshots = screenshots,
                            popularTags = popularTags,
                            hasData = hasData,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                emitEffect(SearchEffect.ShowError("스크린샷을 불러오는 중 오류가 발생했습니다."))
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
                tag.contains(query, ignoreCase = true)
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

        val results = currentState.screenshots.filter { screenshot ->
            // AND 조건: 선택된 모든 태그를 포함하는 스크린샷만 필터링
            selectedTags.all { selectedTag ->
                screenshot.tags.any { tag ->
                    tag.contains(selectedTag, ignoreCase = true)
                }
            }
        }

        updateState { copy(searchResults = results) }
    }

    private fun searchUncategorizedScreenshots() {
        // 태그가 없는 스크린샷들만 필터링
        val results = currentState.screenshots.filter { screenshot ->
            screenshot.tags.isEmpty()
        }

        updateState {
            copy(
                searchResults = results,
                relatedTags = emptyList() // 미분류는 연관 태그 없음
            )
        }
    }

    private fun updateRelatedTags(selectedTags: List<String>) {
        if (selectedTags.isEmpty()) {
            updateState { copy(relatedTags = emptyList()) }
            return
        }

        // 선택된 모든 태그를 포함하는 스크린샷들만 찾기 (AND 로직)
        val matchingScreenshots = currentState.screenshots.filter { screenshot ->
            selectedTags.all { selectedTag ->
                screenshot.tags.any { tag ->
                    tag.contains(selectedTag, ignoreCase = true)
                }
            }
        }

        // 매칭된 스크린샷들의 다른 태그들을 수집 (이미 선택된 태그는 제외)
        val relatedTagsSet = mutableSetOf<String>()
        matchingScreenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                // 이미 선택된 태그는 제외
                if (!selectedTags.any { selectedTag ->
                        tag.equals(selectedTag, ignoreCase = true)
                    }) {
                    relatedTagsSet.add(tag)
                }
            }
        }

        // 빈도순으로 정렬 (옵션)
        val sortedRelatedTags = relatedTagsSet.toList().sortedByDescending { tag ->
            matchingScreenshots.count { screenshot ->
                screenshot.tags.any { it.equals(tag, ignoreCase = true) }
            }
        }

        updateState { copy(relatedTags = sortedRelatedTags) }
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

        // 해당 태그가 존재하는지 확인
        val tagExists = currentState.screenshots.any { screenshot ->
            screenshot.tags.any { tag ->
                tag.contains(query, ignoreCase = true)
            }
        }

        if (tagExists) {
            // 태그가 존재하면 selectedTags에 추가
            addTag(query)
        } else {
            // 태그가 존재하지 않으면 빈 결과로 설정
            updateState {
                copy(
                    searchResults = emptyList(),
                    hasSearched = true
                )
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
                        currentIndex = currentIndex
                    )
                )
            )
        }
    }

    private fun getPopularTags(screenshots: List<com.prography.domain.model.UiScreenshotModel>): List<TagWithCount> {
        val tagCounts = mutableMapOf<String, Int>()

        screenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                tagCounts[tag] = tagCounts.getOrDefault(tag, 0) + 1
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
}