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
                    // Load search results with selected tags
                    val selectedTags = searchState.selectedTags
                    searchResultsViewModel.loadSearchResults(selectedTags)
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
                    // Just clear the search results state, SearchContent will automatically show
                    // Don't call searchViewModel.ClearSearch as it might cause conflicts
                    searchResultsViewModel.sendAction(com.prography.home.ui.search.contract.SearchAction.ClearSearch)
                }

                else -> {
                    // Other effects handled elsewhere
                }
            }
        }
    }

    // Show SearchResultsContent if there are search results, otherwise show SearchContent
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
            TagWithCount(0, "쇼핑", 25),
            TagWithCount(0, "여행", 18),
            TagWithCount(0, "음식", 15),
            TagWithCount(0, "강아지", 12),
            TagWithCount(0, "세상에서 제일 귀여운 강아지들", 8),
            TagWithCount(0, "통키", 6),
            TagWithCount(0, "여러분", 4)
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