package com.prography.home.ui.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.home.ui.search.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllScreenshotsUseCase: GetAllScreenshotsUseCase
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
            copy(searchQuery = query)
        }

        if (query.isNotEmpty()) {
            searchScreenshots(query)
        } else {
            updateState { copy(searchResults = emptyList()) }
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
            } || screenshot.appName.contains(query, ignoreCase = true)
        }

        updateState { copy(searchResults = results) }
    }

    private fun addTag(tag: String) {
        val currentTags = currentState.selectedTags
        if (!currentTags.contains(tag)) {
            val newTags = currentTags + tag
            updateState { copy(selectedTags = newTags) }
            searchBySelectedTags(newTags)
            updateRelatedTags(newTags)
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
            searchBySelectedTags(newTags)
            updateRelatedTags(newTags)
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

    private fun updateRelatedTags(selectedTags: List<String>) {
        if (selectedTags.isEmpty()) {
            updateState { copy(relatedTags = emptyList()) }
            return
        }

        // 선택된 태그를 포함하는 스크린샷들의 다른 태그들을 수집
        val relatedTagsSet = mutableSetOf<String>()

        currentState.screenshots.forEach { screenshot ->
            // 선택된 태그 중 하나라도 포함하는 스크린샷의 모든 태그를 수집
            if (selectedTags.any { selectedTag ->
                    screenshot.tags.any { tag ->
                        tag.contains(selectedTag, ignoreCase = true)
                    }
                }) {
                screenshot.tags.forEach { tag ->
                    // 이미 선택된 태그는 제외
                    if (!selectedTags.contains(tag)) {
                        relatedTagsSet.add(tag)
                    }
                }
            }
        }

        updateState { copy(relatedTags = relatedTagsSet.toList()) }
    }

    private fun clearSearch() {
        updateState {
            copy(
                searchQuery = "",
                selectedTags = emptyList(),
                searchResults = emptyList(),
                relatedTags = emptyList()
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

        return tagCounts.entries
            .map { TagWithCount(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(10) // 상위 10개 태그만
    }
}