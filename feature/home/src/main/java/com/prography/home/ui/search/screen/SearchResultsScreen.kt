package com.prography.home.ui.search.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.search.contract.SearchEffect
import com.prography.home.ui.search.viewmodel.SearchResultsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchResultsScreen(
    query: String,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateToStorage: () -> Unit = {},
    searchResultsViewModel: SearchResultsViewModel = hiltViewModel()
) {
    val state by searchResultsViewModel.uiState.collectAsState()
    val effectFlow = searchResultsViewModel.effect

    // Load search results when screen is first displayed
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            val tags = query.split(",").filter { it.isNotEmpty() }
            searchResultsViewModel.loadSearchResults(tags)
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is SearchEffect.ShowError -> {
                    println("Search Results Error: ${effect.message}")
                }

                is SearchEffect.NavigateToStorage -> {
                    onNavigateToStorage()
                }

                is SearchEffect.NavigateBackToSearch -> {
                    onNavigateUp()
                }

                else -> {
                    // Other effects handled elsewhere
                }
            }
        }
    }

    SearchResultsContent(
        state = state,
        onAction = { action ->
            searchResultsViewModel.sendAction(action)
        },
        modifier = modifier
    )
}