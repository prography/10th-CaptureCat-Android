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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.search.contract.*
import com.prography.ui.component.*
import com.prography.ui.theme.*

@Composable
fun SearchContent(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding() // Add navigationBarsPadding here
    ) {
        if (state.selectedTags.isNotEmpty()) {
            // 기존 SearchBar 숨기고 이걸로 대체
            SelectedTagsSearchHeader(
                selectedTags = state.selectedTags,
                onRemoveTag = { tag -> onAction(SearchAction.RemoveTag(tag)) },
                onClearAll = { onAction(SearchAction.ClearSearch) }
            )
        } else {
            UiSearchBar(
                value = state.searchQuery,
                onValueChange = { onAction(SearchAction.UpdateSearchQuery(it)) },
                placeholder = "태그 이름으로 검색해 보세요",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            )
        }

        val isSearchMode = state.selectedTags.isNotEmpty() || state.searchQuery.isNotEmpty()

        when {
            !state.hasData || state.popularTags.isEmpty() -> {
                // 초기 Empty 상태
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTagsState()
                }
            }

            isSearchMode && state.searchResults.isEmpty() -> {
                // 검색했는데 결과 없을 때
                EmptySearchResult(
                    onClearSearch = { onAction(SearchAction.ClearSearch) }
                )
            }

            else -> {
                // 정상 결과 또는 인기 태그
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
        modifier = Modifier
            .fillMaxSize(),
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
                // 검색 결과를 2개씩 묶어서 표시
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
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // 한 개만 있는 경우 빈 공간 추가
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
                            contentDescription = "태그 제거",
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
            text = "취소",
            style = body02Regular,
            color = Text02,
            modifier = Modifier
                .clickable { onClearAll() }
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
            // 이미 선택된 태그들은 제외하고 표시
            // relatedTags는 현재 선택된 모든 태그를 가진 스크린샷들의 다른 태그들이어야 함
            // 예: 선택된 태그 ["여행", "서울"] → 이 두 태그를 모두 가진 스크린샷들의 다른 태그들 ["카페", "한강", "맛집"]
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
            text = "태그 바로가기",
            style = subhead01Bold,
            color = Text02,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp, start =16.dp)
        )

        LazyRow(
            modifier = Modifier
                .padding(vertical = 8.dp),
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
fun EmptyTagsState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "아직 태그가 없어요.",
                style = headline02Bold,
                color = Text02,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "스크린샷을 태그해 정리해보세요!",
                style = body01Regular,
                color = Text03,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            UiLabelAddButton(
                text = "임시보관함 가기",
                onClick = { /* TODO */ }
            )
        }
    }
}


@Composable
fun EmptySearchResult(
    onClearSearch: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "검색 결과가 없어요.",
                style = subhead01Bold,
                color = Text01,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "다른 키워드로 검색해보세요.",
                style = body02Regular,
                color = Text03,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            UiPrimaryButton(
                text = "검색 초기화",
                onClick = onClearSearch,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

@Composable
fun SearchResultItem(
    screenshot: UiScreenshotModel,
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
    ) {
        Image(
            painter = rememberAsyncImagePainter(screenshot.uri),
            contentDescription = screenshot.appName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Tags at bottom
        if (screenshot.tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 8.dp)
            ) {
                screenshot.tags.take(2).forEach { tag ->
                    UiTagInfoChip(text = tag)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchContentPreview() {
    val sampleState = SearchState(
        popularTags = listOf(
            TagWithCount("쇼핑", 15),
            TagWithCount("여행", 8),
            TagWithCount("음식", 6),
            TagWithCount("가나다라마바사마바사아자타카하", 4)
        ),
        hasData = true
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
fun EmptySearchContentPreview() {
    PrographyTheme {
        SearchContent(
            state = SearchState(),
            onAction = {}
        )
    }
}