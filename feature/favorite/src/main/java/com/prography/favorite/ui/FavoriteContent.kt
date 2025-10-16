package com.prography.favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.prography.domain.model.TagModel
import com.prography.favorite.ui.contract.FavoriteAction
import com.prography.favorite.ui.contract.FavoriteState
import com.prography.domain.model.UiScreenshotModel
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.*
import timber.log.Timber
import com.prography.ui.R as UiR

@Composable
fun FavoriteContent(
    state: FavoriteState,
    onAction: (FavoriteAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Text(
            text = stringResource(UiR.string.favorite_title),
            style = headline02Bold,
            color = Text01,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )

        // ▼ 태그 탭바
        FavoriteTabSection(
            popularTags = state.popularTags,
            selectedTag = state.selectedTag,
            onTagSelected = { tag -> onAction(FavoriteAction.OnTagSelected(tag)) },
            onTagSettingClick = { onAction(FavoriteAction.NavigateToTagSetting) }
        )

        when {
            !state.hasData -> {
                // 전체 즐겨찾기 자체가 비어있는 경우
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(UiR.string.favorite_empty_title),
                            style = headline02Bold,
                            color = Text02,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(UiR.string.favorite_empty_info),
                            style = body01Regular,
                            color = Text03,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            state.displayedScreenshots.isEmpty() -> {
                // 즐겨찾기는 있지만 현재 선택된 태그 결과가 없는 경우
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "선택한 태그에 해당하는 즐겨찾기가 없어요",
                        style = body01Regular,
                        color = Text03
                    )
                }
            }
            else -> {
                // Grid content (2열)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val chunked = state.displayedScreenshots.chunked(2)
                    items(chunked) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { screenshot ->
                                FavoriteScreenshotItem(
                                    screenshot = screenshot,
                                    onScreenshotClick = {
                                        onAction(FavoriteAction.OnScreenshotClick(screenshot))
                                    },
                                    onToggleFavorite = {
                                        onAction(FavoriteAction.OnToggleFavorite(screenshot))
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteTabSection(
    popularTags: List<com.prography.domain.model.TagWithCount>,
    selectedTag: TagModel?,                                 // ✅ TagModel?
    onTagSelected: (TagModel?) -> Unit,                     // ✅ "전체"는 null
    onTagSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "전체" 탭 (null)
                item {
                    FavTagChip(
                        text = "전체", // "전체"
                        isSelected = selectedTag == null,
                        onClick = { onTagSelected(null) }
                    )
                }

                // 인기 태그 탭들
                items(popularTags.size) { idx ->
                    val t = popularTags[idx]
                    val tagModel = TagModel(id = t.id?.toLong() ?: 0, name = t.tag)
                    
                    FavTagChip(
                        text = tagModel.name,
                        isSelected = selectedTag?.name.equals(tagModel.name, ignoreCase = true),
                        onClick = { onTagSelected(tagModel) }
                    )
                }
            }

            // 오른쪽 페이드
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(16.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0f),
                                    Color.White.copy(alpha = 1f)
                                )
                            )
                        )
                )
            }
        }

        // 구분선
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 6.dp)
                .width(1.dp)
                .height(10.dp)
                .background(Gray03)
        )

        Icon(
            painter = painterResource(id = UiR.drawable.ic_home_tag_tab),
            contentDescription = "태그 설정",
            tint = Color.Unspecified,
            modifier = Modifier.clickableWithoutRipple { onTagSettingClick() }
        )
    }
}

// 그대로 써도 되는 Chip
@Composable
private fun FavTagChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            modifier = modifier
                .clickableWithoutRipple { onClick() }
                .padding(top = 2.dp, bottom = 10.dp),
            style = subhead02Bold,
            color = if (isSelected) Primary else Text03,
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = if (isSelected) Primary else Color.Transparent,
            thickness = 3.dp
        )
    }
}



@Composable
fun FavoriteScreenshotItem(
    screenshot: UiScreenshotModel,
    onScreenshotClick: () -> Unit,
    onToggleFavorite: () -> Unit,
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray01),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(screenshot.uri),
                contentDescription = null ,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 즐겨찾기 아이콘 (우상단)
        Icon(
            painter = painterResource(id = com.prography.ui.R.drawable.favorite_white),
            contentDescription = stringResource(com.prography.ui.R.string.favorite_icon),
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .size(32.dp)
                .clickableWithoutRipple {
                    onToggleFavorite()
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteContentPreview() {
    FavoriteContent(
        state = FavoriteState(
            favoriteScreenshots = emptyList(),
            hasData = false
        ),
        onAction = {}
    )
}