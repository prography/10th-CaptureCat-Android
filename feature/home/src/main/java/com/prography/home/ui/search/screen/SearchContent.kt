// SearchContent.kt

package com.prography.home.ui.search.screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.prography.domain.model.AutocompleteTagModel
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.search.contract.*
import com.prography.ui.R
import com.prography.ui.component.*
import com.prography.ui.theme.*
import com.prography.util.SearchRefreshManager
import com.prography.util.SearchRefreshWrapper
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import com.prography.ui.R as UiR

@Composable
fun SearchContent(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchRefreshManager: SearchRefreshManager =
        hiltViewModel<SearchRefreshWrapper>().searchRefreshManager
    var lastProcessedTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        searchRefreshManager.refreshEvent.collect {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastProcessedTime > 1000) {
                lastProcessedTime = currentTime
                if (state.selectedTags.isNotEmpty()) {
                    onAction(SearchAction.RefreshSearchResults)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        UiHeader(
            title = stringResource(id = R.string.search_title)
        )

        UiSearchBar(
            value = state.searchQuery,
            onValueChange = { onAction(SearchAction.UpdateSearchQuery(it)) },
            onSearchComplete = { onAction(SearchAction.OnSearchComplete) },
            placeholder = stringResource(UiR.string.search_placeholder),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
        )

        when {
            state.popularTags.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    UiEmptyState(
                        title = stringResource(id = UiR.string.empty_popular_tag_title),
                        info = stringResource(id = UiR.string.empty_popular_tag_info),
                        onClick = { onAction(SearchAction.NavigateToStorage) }
                    )
                }
            }

            state.showAutocomplete && state.autocompleteResults.isNotEmpty() -> {
                // 중간단: 자동완성 결과 표시
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.autocompleteResults) { autocompleteTag ->
                        AutocompleteTagItem(
                            tag = autocompleteTag,
                            onTagClick = { tag ->
                                onAction(SearchAction.AddTag(tag.name))
                            }
                        )
                    }
                }
            }

            state.showAutocomplete && state.autocompleteResults.isEmpty() -> {
                // 검색어는 있지만 자동완성 결과가 없는 경우
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    UiEmptyState(
                        title = stringResource(id = UiR.string.empty_search_result_title),
                        info = stringResource(id = UiR.string.empty_search_result_info),
                        buttonText = "",
                        onClick = { }
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (state.popularTags.isNotEmpty()) {
                        item {
                            PopularTagsSection(
                                tags = state.popularTags,
                                onTagClick = { tag -> onAction(SearchAction.AddTag(tag)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutocompleteTagItem(
    tag: AutocompleteTagModel,
    onTagClick: (AutocompleteTagModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTagClick(tag) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tag.name,
            style = body01Regular,
            color = Text01,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_outward),
            contentDescription = null,
            tint = Text02
        )
    }
}

@Composable
fun PopularTagsSection(
    tags: List<TagWithCount>,
    onTagClick: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(id = UiR.string.tag_shortcut),
            style = subhead01Bold,
            color = Text02,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp)
        )

        FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tags.forEach { tagWithCount ->
                UiTagShortcutChip(
                    text = tagWithCount.tag,
                    onClick = { onTagClick(tagWithCount.tag) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchContentInitialPreview() {
    val sampleState = SearchState(
        searchQuery = "",
        popularTags = listOf(
            TagWithCount(0, "쇼핑", 25),
            TagWithCount(0, "여행", 18),
            TagWithCount(0, "음식", 15),
            TagWithCount(0, "강아지", 12),
            TagWithCount(0, "세상에서 제일 귀여운 강아지들", 8),
            TagWithCount(0, "통키", 6),
            TagWithCount(0, "여러분", 4)
        ),
        showAutocomplete = false,
        autocompleteResults = emptyList(),
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
fun SearchContentAutocompletePreview() {
    val sampleState = SearchState(
        searchQuery = "자",
        popularTags = listOf(
            TagWithCount(0, "쇼핑", 25),
            TagWithCount(0, "여행", 18)
        ),
        showAutocomplete = true,
        autocompleteResults = listOf(
            AutocompleteTagModel(1, "자바"),
            AutocompleteTagModel(2, "자바스크립트"),
            AutocompleteTagModel(3, "자동차"),
            AutocompleteTagModel(4, "자료구조"),
            AutocompleteTagModel(5, "자연")
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
fun SearchContentEmptyAutocompletePreview() {
    val sampleState = SearchState(
        searchQuery = "없는검색어",
        popularTags = listOf(
            TagWithCount(0, "쇼핑", 25),
            TagWithCount(0, "여행", 18)
        ),
        showAutocomplete = true,
        autocompleteResults = emptyList(),
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
fun SearchContentEmptyTagsPreview() {
    val sampleState = SearchState(
        searchQuery = "",
        popularTags = emptyList(),
        showAutocomplete = false,
        autocompleteResults = emptyList(),
        isLoading = false
    )

    PrographyTheme {
        SearchContent(
            state = sampleState,
            onAction = {}
        )
    }
}
