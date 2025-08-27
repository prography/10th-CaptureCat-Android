// SearchContent.kt

package com.prography.home.ui.search.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.search.contract.*
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = UiR.string.search_title),
                style = headline02Bold,
                color = Text01
            )
        }

        if (state.selectedTags.isNotEmpty()) {
            SelectedTagsSearchHeader(
                selectedTags = state.selectedTags,
                onRemoveTag = { tag -> onAction(SearchAction.RemoveTag(tag)) },
                onClearAll = { onAction(SearchAction.ClearSearch) }
            )
        } else {
            UiSearchBar(
                value = state.searchQuery,
                onValueChange = { onAction(SearchAction.UpdateSearchQuery(it)) },
                onSearchComplete = { onAction(SearchAction.OnSearchComplete) },
                placeholder = stringResource(UiR.string.search_placeholder),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            )
        }

        val isSearchMode = state.selectedTags.isNotEmpty() || state.searchResults.isNotEmpty()

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

            state.hasSearched && state.searchResults.isEmpty() -> {
                UiEmptyState(
                    title = stringResource(id = UiR.string.empty_search_result_title),
                    info = stringResource(id = UiR.string.empty_search_result_info),
                    buttonText = "",
                    onClick = { onAction(SearchAction.NavigateToStorage) }
                )
            }

            else -> {
                SearchResultsContent(
                    state = state,
                    isSearchMode = isSearchMode,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    state: SearchState,
    isSearchMode: Boolean,
    onAction: (SearchAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (isSearchMode) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray02)
                )
            }

            if (state.relatedTags.isNotEmpty()) {
                item {
                    RelatedTagsSection(
                        relatedTags = state.relatedTags,
                        selectedTags = state.selectedTags,
                        onTagClick = { tag -> onAction(SearchAction.AddTag(tag)) }
                    )
                }
            }

            if (state.searchResults.isNotEmpty()) {
                val chunkedResults = state.searchResults.chunked(2)
                items(chunkedResults) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { screenshot ->
                            SearchResultItem(
                                screenshot = screenshot,
                                onScreenshotClick = {
                                    onAction(SearchAction.OnScreenshotClick(screenshot))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
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

@Composable
fun SelectedTagsSearchHeader(
    selectedTags: List<String>,
    onRemoveTag: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Gray03, RoundedCornerShape(6.dp))
                .background(Gray01, RoundedCornerShape(6.dp))
                .padding(vertical = 3.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(selectedTags) { tag ->
                    Row(
                        modifier = Modifier
                            .background(Background, RoundedCornerShape(30.dp))
                            .border(1.dp, Primary, RoundedCornerShape(30.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = tag,
                            style = body02Regular,
                            color = Primary
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(UiR.string.remove_tag),
                            tint = Primary,
                            modifier = Modifier
                                .size(14.sp.value.dp)
                                .clickable { onRemoveTag(tag) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(UiR.string.cancel),
            style = body02Regular,
            color = Text02,
            modifier = Modifier.clickable { onClearAll() }
        )
    }
}

@Composable
fun RelatedTagsSection(
    relatedTags: List<String>,
    selectedTags: List<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(relatedTags.filter { !selectedTags.contains(it) }) { tag ->
                UiTagShortcutChip(
                    text = tag,
                    onClick = { onTagClick(tag) }
                )
            }
        }
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
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp, start = 16.dp)
        )

        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(tags) { tagWithCount ->
                UiTagShortcutChip(
                    text = tagWithCount.tag,
                    onClick = { onTagClick(tagWithCount.tag) }
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    screenshot: UiScreenshotModel,
    onScreenshotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(45f / 76f)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 0.75.dp,
                color = Gray02,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onScreenshotClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(screenshot.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (screenshot.tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 8.dp)
            ) {
                screenshot.tags.forEach { tag ->
                    UiTagInfoChip(text = tag.name)
                }
            }
        }
    }
}
