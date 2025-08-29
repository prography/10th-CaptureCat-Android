package com.prography.home.ui.search.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.TagModel
import com.prography.domain.model.TagWithCount
import com.prography.home.ui.search.contract.SearchEffect
import com.prography.home.ui.search.contract.SearchState
import com.prography.home.ui.search.viewmodel.SearchViewModel
import com.prography.home.ui.search.viewmodel.SearchResultsViewModel
import com.prography.home.ui.search.screen.SearchResultsContent
import com.prography.home.ui.search.screen.SearchContent
import com.prography.ui.theme.PrographyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToStorage: () -> Unit = {},
    searchViewModel: SearchViewModel = hiltViewModel(),
    searchResultsViewModel: SearchResultsViewModel = hiltViewModel()
) {
    val searchState by searchViewModel.uiState.collectAsState()
    val searchResultsState by searchResultsViewModel.uiState.collectAsState()
    val searchEffectFlow = searchViewModel.effect
    val searchResultsEffectFlow = searchResultsViewModel.effect

    // Handle search effects
    LaunchedEffect(Unit) {
        searchViewModel.loadMostUsedTags()
        searchEffectFlow.collectLatest { effect ->
            when (effect) {
                is SearchEffect.ShowError -> {
                    println("Search Error: ${effect.message}")
                }
                is SearchEffect.NavigateToStorage -> {
                    onNavigateToStorage()
                }
                is SearchEffect.NavigateToSearchResults -> {
                    // Load search results in the results viewmodel
                    searchResultsViewModel.loadSearchResults(searchState.selectedTags)
                }

                else -> {
                    // Other effects handled elsewhere
                }
            }
        }
    }

    // Handle search results effects
    LaunchedEffect(Unit) {
        searchResultsEffectFlow.collectLatest { effect ->
            when (effect) {
                is SearchEffect.ShowError -> {
                    println("Search Results Error: ${effect.message}")
                }

                is SearchEffect.NavigateToStorage -> {
                    onNavigateToStorage()
                }

                is SearchEffect.NavigateBackToSearch -> {
                    // Reset both search and search results state to show search content
                    searchViewModel.sendAction(com.prography.home.ui.search.contract.SearchAction.ClearSearch)
                    // SearchResultsViewModel state will be cleared by its own clearSearch call
                }

                else -> {
                    // Other effects handled elsewhere
                }
            }
        }
    }

    // Show SearchResultsContent if we have search results, otherwise show SearchContent
    if (searchResultsState.hasSearched && searchResultsState.selectedTags.isNotEmpty()) {
        SearchResultsContent(
            state = searchResultsState,
            onAction = { action ->
                searchResultsViewModel.sendAction(action)
            },
            modifier = modifier
        )
    } else {
        SearchContent(
            state = searchState,
            onAction = { action ->
                searchViewModel.sendAction(action)
            },
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenWithTagsPreview() {
    val sampleState = SearchState(
        popularTags = listOf(
            TagWithCount("쇼핑", 25),
            TagWithCount("여행", 18),
            TagWithCount("음식", 15),
            TagWithCount("강아지", 12),
            TagWithCount("세상에서 제일 귀여운 강아지들", 8),
            TagWithCount("통키", 6),
            TagWithCount("여러분", 4)
        ),
        isLoading = false
    )

    PrographyTheme {
        SearchContent(
            state = sampleState,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenEmptyPreview() {
    PrographyTheme {
        SearchContent(
            state = SearchState(
                isLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenWithResultsPreview() {
    val sampleScreenshots = listOf(
        com.prography.domain.model.UiScreenshotModel(
            id = "1",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
            isBookmarked = false,
            dateStr = "2024-01-15"
        ),
        com.prography.domain.model.UiScreenshotModel(
            id = "2",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
            isBookmarked = true,
            dateStr = "2024-01-14"
        ),
        com.prography.domain.model.UiScreenshotModel(
            id = "3",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
            isBookmarked = false,
            dateStr = "2024-01-13"
        )
    )

    PrographyTheme {
        SearchResultsContent(
            state = SearchState(
                searchQuery = "",
                selectedTags = listOf("쇼핑"),
                relatedTags = listOf("패션", "예쁜가방", "서울", "서울숲", "옷", "콜라보"),
                searchResults = sampleScreenshots,
                hasSearched = true,
                isLoading = false
            ),
            onAction = {}
        )
    }
}