package com.prography.imageDetail.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prography.domain.model.UiScreenshotModel
import com.prography.imageDetail.ui.contract.ImageDetailAction
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.ui.R
import com.prography.ui.component.UiTagInfoChip
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.component.DeleteConfirmDialog
import com.prography.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.screenshots.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (page != state.currentIndex) {
                onAction(ImageDetailAction.OnPageChange(page))
            }
        }
    }

    LaunchedEffect(state.currentIndex) {
        if (pagerState.currentPage != state.currentIndex) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Secondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ImageDetailHeader(
                date = state.currentScreenshot?.dateStr.orEmpty(),
                onBack = { onAction(ImageDetailAction.OnNavigateBack) }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ScreenshotPager(
                    state = state,
                    pagerState = pagerState
                )

                ChipSection(
                    tags = state.currentScreenshot?.tags.orEmpty(),
                    isFavorite = state.currentScreenshot?.isFavorite == true,
                    onFavoriteToggle = { onAction(ImageDetailAction.OnToggleFavorite) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )
            }

            BottomActionBar(
                onEditTagClick = { onAction(ImageDetailAction.OnShowTagEditBottomSheet) },
                onDeleteClick = { onAction(ImageDetailAction.OnDeleteScreenshot) }
            )
        }

        if (state.isTagEditBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { onAction(ImageDetailAction.OnHideTagEditBottomSheet) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PureWhite
            ) {
                TagEditBottomSheetContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 삭제 확인 다이얼로그
        DeleteConfirmDialog(
            isVisible = state.isDeleteDialogVisible,
            selectedCount = 1,
            onDismiss = { onAction(ImageDetailAction.OnHideDeleteDialog) },
            onConfirm = { onAction(ImageDetailAction.OnConfirmDelete) }
        )
    }
}

@Composable
fun ImageDetailHeader(date: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_white_back),
            contentDescription = "뒤로가기",
            tint = PureWhite,
            modifier = Modifier.clickableWithoutRipple(enabled = true, onClick = onBack)
        )
        Text(
            text = date,
            style = body02Regular,
            color = PureWhite
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenshotPager(state: ImageDetailState, pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val screenshot = state.screenshots.getOrNull(page)
        if (screenshot != null) {
            AsyncImage(
                model = screenshot.uri,
                contentDescription = "스크린샷",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ChipSection(
    tags: List<String>,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tags) { tag ->
                    UiTagInfoChip(text = tag)
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        IconButton(onClick = onFavoriteToggle) {
            Icon(
                painter = painterResource(
                    id = if (isFavorite) R.drawable.ic_detail_favorite_checked else R.drawable.ic_detail_favorite_unchecked
                ),
                contentDescription = "즐겨찾기",
                tint = OverlayDim
            )
        }
    }
}


@Composable
fun BottomActionBar(
    onEditTagClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 태그편집
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag_edit),
                    contentDescription = "태그편집",
                    tint = PureWhite,
                    modifier = Modifier.clickableWithoutRipple(enabled = true, onClick = onEditTagClick)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "태그편집",
                    color = PureWhite,
                    style = caption02Regular
                )
            }
        }

        // 삭제
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "삭제",
                    tint = PureWhite,
                    modifier = Modifier.clickableWithoutRipple(enabled = true, onClick = onDeleteClick)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "삭제",
                    color = PureWhite,
                    style = caption02Regular
                )
            }
        }
    }
}

@Composable
private fun TagEditBottomSheetContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "태그 추가",
                style = subhead01Bold,
                color = Text01
            )

            IconButton(
                onClick = { onAction(ImageDetailAction.OnHideTagEditBottomSheet) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "닫기",
                    tint = Gray06
                )
            }
        }

        // 태그 입력 필드
        BasicTextField(
            value = state.newTagText,
            onValueChange = { onAction(ImageDetailAction.OnNewTagTextChange(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Gray04,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            textStyle = body02Regular.copy(color = Text01),
            decorationBox = { innerTextField ->
                Box {
                    if (state.newTagText.isEmpty()) {
                        Text(
                            text = "태그를 입력하세요",
                            style = body02Regular,
                            color = Gray06
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 추가 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (state.newTagText.isNotEmpty()) Primary else Gray04,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = state.newTagText.isNotEmpty()) {
                    onAction(ImageDetailAction.OnAddNewTag)
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "추가",
                style = subhead02Bold,
                color = if (state.newTagText.isNotEmpty()) PureWhite else Gray06
            )
        }

        // 추가된 태그들
        if (state.currentScreenshot?.tags?.isNotEmpty() == true) {
            Text(
                text = "추가된 태그",
                style = subhead02Bold,
                color = Text01
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.currentScreenshot.tags) { tag ->
                    UiTagSelectedChip(
                        text = tag,
                        onDelete = { onAction(ImageDetailAction.OnTagDelete(tag)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun ImageDetailScreenPreview() {
    val sampleScreenshots = listOf(
        UiScreenshotModel(
            id = "1",
            uri = "https://via.placeholder.com/300x400",
            appName = "Instagram",
            tags = listOf("쇼핑", "패션"),
            isFavorite = false,
            dateStr = "2024년 1월 15일"
        ),
        UiScreenshotModel(
            id = "2",
            uri = "https://via.placeholder.com/300x400",
            appName = "YouTube",
            tags = listOf("여행", "음식"),
            isFavorite = true,
            dateStr = "2024년 1월 14일"
        ),
        UiScreenshotModel(
            id = "3",
            uri = "https://via.placeholder.com/300x400",
            appName = "Coupang",
            tags = listOf("쇼핑", "생활용품"),
            isFavorite = false,
            dateStr = "2024년 1월 13일"
        )
    )

    PrographyTheme {
        ImageDetailContent(
            state = ImageDetailState(
                screenshots = sampleScreenshots,
                currentIndex = 0,
                currentScreenshot = sampleScreenshots.first(),
                availableTags = listOf("쇼핑", "패션", "여행", "음식", "생활용품"),
                isTagEditBottomSheetVisible = false,
                newTagText = "",
                isLoading = false,
                isDeleteDialogVisible = false
            ),
            onAction = {}
        )
    }
}