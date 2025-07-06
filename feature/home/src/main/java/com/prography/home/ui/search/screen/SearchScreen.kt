package com.prography.home.ui.search.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.search.contract.SearchEffect
import com.prography.home.ui.search.contract.SearchState
import com.prography.home.ui.search.contract.TagWithCount
import com.prography.home.ui.search.viewmodel.SearchViewModel
import com.prography.ui.theme.PrographyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // Handle effects
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is SearchEffect.ShowError -> {
                    // TODO: Show error message (could use SnackBar or Toast)
                    // For now, just log the error
                    println("Search Error: ${effect.message}")
                }
            }
        }
    }

    SearchContent(
        state = state,
        onAction = { action ->
            viewModel.sendAction(action)
        },
        modifier = modifier
    )
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
        hasData = true,
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
                hasData = false,
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
            appName = "Instagram",
            tags = listOf("쇼핑", "패션"),
            isFavorite = false,
            dateStr = "2024-01-15"
        ),
        com.prography.domain.model.UiScreenshotModel(
            id = "2",
            uri = "https://via.placeholder.com/300x400",
            appName = "YouTube",
            tags = listOf("쇼핑"),
            isFavorite = true,
            dateStr = "2024-01-14"
        ),
        com.prography.domain.model.UiScreenshotModel(
            id = "3",
            uri = "https://via.placeholder.com/300x400",
            appName = "Coupang",
            tags = listOf("쇼핑", "생활용품"),
            isFavorite = false,
            dateStr = "2024-01-13"
        )
    )

    PrographyTheme {
        SearchContent(
            state = SearchState(
                searchQuery = "",
                selectedTags = listOf("쇼핑"),
                relatedTags = listOf("패션", "예쁜가방", "서울", "서울숲", "옷", "콜라보"),
                searchResults = sampleScreenshots,
                hasData = true,
                isLoading = false
            ),
            onAction = {}
        )
    }
}