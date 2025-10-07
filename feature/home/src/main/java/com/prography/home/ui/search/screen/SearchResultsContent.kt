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
import com.prography.domain.model.TagModel
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
fun SearchResultsContent(
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


        SelectedTagsSearchHeader(
            selectedTags = state.selectedTags,
            onRemoveTag = { tag -> onAction(SearchAction.RemoveTag(tag)) },
            onClearAll = { onAction(SearchAction.ClearSearch) },
            onBackClick = { onAction(SearchAction.NavigateBackToSearch) }
        )

        when {
            state.searchResults.isEmpty() -> {
                UiEmptyState(
                    title = stringResource(id = UiR.string.empty_search_result_title),
                    info = stringResource(id = UiR.string.empty_search_result_info)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
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
                }
            }
        }
    }
}

@Composable
fun SelectedTagsSearchHeader(
    selectedTags: List<String>,
    onRemoveTag: (String) -> Unit,
    onClearAll: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(UiR.drawable.ic_arrow_backward),
            contentDescription = "뒤로가기",
            tint = Text02,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Gray03, RoundedCornerShape(6.dp))
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
                            contentDescription = "태그 삭제",
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
