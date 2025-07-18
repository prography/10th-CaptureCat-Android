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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.prography.ui.component.TagInputField
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiTagInfoChip
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.component.DeleteConfirmDialog
import com.prography.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import com.prography.domain.model.TagModel

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ImageDetailContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = if (state.screenshots.isNotEmpty()) {
            state.currentIndex.coerceIn(0, state.screenshots.size - 1)
        } else 0,
        pageCount = { maxOf(1, state.screenshots.size) }
    )

    // Debug logging
    LaunchedEffect(state.currentIndex, state.screenshots.size, state.isLoading) {
        println("ImageDetailContent - currentIndex: ${state.currentIndex}, screenshots.size: ${state.screenshots.size}, pagerPage: ${pagerState.currentPage}, isLoading: ${state.isLoading}")
    }

    // Debug current screenshot
    LaunchedEffect(state.currentScreenshot) {
        println("DEBUG: currentScreenshot changed - id: ${state.currentScreenshot?.id}, tags: ${state.currentScreenshot?.tags}")
    }

    // 페이지 변경 감지 (로딩 중이 아닐 때만)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            if (page != state.currentIndex && state.screenshots.isNotEmpty() && !state.isLoading) {
                println("Pager page changed to: $page, current state index: ${state.currentIndex}")
                onAction(ImageDetailAction.OnPageChange(page))
            }
        }
    }

    // 외부에서 인덱스 변경 시 pager 업데이트 (초기 로딩 완료 후에만)
    LaunchedEffect(state.currentIndex, state.screenshots.size, state.isLoading) {
        if (state.screenshots.isNotEmpty() && !state.isLoading) {
            val targetPage = state.currentIndex.coerceIn(0, state.screenshots.size - 1)
            if (pagerState.currentPage != targetPage) {
                println("Animating to page: $targetPage, current pager page: ${pagerState.currentPage}")
                pagerState.animateScrollToPage(targetPage)
            }
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

                // Get current screenshot directly from the list using current page
                val currentPageScreenshot = if (pagerState.currentPage < state.screenshots.size) {
                    state.screenshots.getOrNull(pagerState.currentPage)
                } else {
                    state.currentScreenshot
                }

                // Debug current page screenshot
                LaunchedEffect(currentPageScreenshot, pagerState.currentPage) {
                    println("DEBUG: currentPageScreenshot changed - page: ${pagerState.currentPage}, id: ${currentPageScreenshot?.id}, tags: ${currentPageScreenshot?.tags}")
                }

                ChipSection(
                    tags = currentPageScreenshot?.tags.orEmpty(),
                    isFavorite = currentPageScreenshot?.isFavorite == true,
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
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = Color.White,
                tonalElevation = 0.dp,
                dragHandle = null,
                modifier = Modifier.imePadding()
            ) {
                TagEditBottomSheetContent(
                    state = state,
                    onAction = onAction
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
    tags: List<TagModel>,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Debug logging
    LaunchedEffect(tags) {
        println("DEBUG: ChipSection - tags updated: $tags")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                tags.forEach { tag ->
                    UiTagInfoChip(text = tag.name)
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        IconButton(onClick = onFavoriteToggle) {
            Icon(
                painter = painterResource(
                    id = if (isFavorite) R.drawable.ic_favorite_check else R.drawable.ic_favorite_uncheck
                ),
                contentDescription = "즐겨찾기",
                tint = Color.Unspecified
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TagEditBottomSheetContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isMaxTagsReached = (state.currentScreenshot?.tags?.size ?: 0) >= 4
    
    // 키보드 상태 확인
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeVisible by remember {
        derivedStateOf { ime.getBottom(density) > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp) // 완료 버튼 여백
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "태그 추가",
                style = headline03Bold,
                color = Text01
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "닫기",
                tint = Text01,
                modifier = Modifier
                    .size(24.dp)
                    .clickableWithoutRipple { onAction(ImageDetailAction.OnHideTagEditBottomSheet) }
            )
        }

        // 입력창
        TagInputField(
            value = state.newTagText,
            onValueChange = {
                if (!isMaxTagsReached) {
                    onAction(ImageDetailAction.OnNewTagTextChange(it))
                }
            },
            placeholder = "추가할 태그를 입력해주세요",
            errorMessage = null,
            onClear = { onAction(ImageDetailAction.OnNewTagTextChange("")) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            enabled = !isMaxTagsReached,
            onDone = {
                onAction(ImageDetailAction.OnAddNewTag)
                focusManager.clearFocus()
            }
        )

        // 태그 목록 (입력창에 포커스 없고 키보드도 내려갔을 때만)
        if (!imeVisible && state.currentScreenshot?.tags?.isNotEmpty() == true) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "추가한 태그",
                        style = subhead01Bold,
                        color = Text01
                    )
                    Text(
                        text = "태그는 최대 4개까지 지정할 수 있어요",
                        style = caption02Regular,
                        color = Text03
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.currentScreenshot.tags.forEach { tag ->
                        UiTagSelectedChip(
                            text = tag.name,
                            onDelete = { onAction(ImageDetailAction.OnTagDelete(tag)) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imeVisible) {
            UiBottomInputButton(
                text = "완료",
                enabled = state.newTagText.isNotBlank(),
                onClick = {
                    if (state.newTagText.isNotBlank()) {
                        onAction(ImageDetailAction.OnAddNewTag)
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }

    // 강제 포커스 제거 (→ 사용자가 눌러야 키보드 뜸)
    // 필요 시 직접 클릭으로 포커스 유도
}



@Preview(showBackground = true)
@Composable
fun ImageDetailScreenPreview() {
    val sampleScreenshots = listOf(
        UiScreenshotModel(
            id = "1",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
            isFavorite = false,
            dateStr = "2024년 1월 15일"
        ),
        UiScreenshotModel(
            id = "2",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
            isFavorite = true,
            dateStr = "2024년 1월 14일"
        ),
        UiScreenshotModel(
            id = "3",
            uri = "https://via.placeholder.com/300x400",
            tags = listOf(TagModel("1","쇼핑"), TagModel("2","패션")),
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