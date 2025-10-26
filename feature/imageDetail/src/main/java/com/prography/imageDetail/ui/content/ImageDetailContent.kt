package com.prography.imageDetail.ui.content

import TagEditContent
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
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
import com.prography.ui.component.UiImageDetailTagChip
import com.prography.ui.component.UnderlinedClickableText
import timber.log.Timber

@OptIn(
    ExperimentalMaterial3Api::class,
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
                println("Scrolling to page: $targetPage, current pager page: ${pagerState.currentPage}")
                pagerState.scrollToPage(targetPage)
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
                    isFavorite = currentPageScreenshot?.isBookmarked == true,
                    onFavoriteToggle = { onAction(ImageDetailAction.OnToggleFavorite) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )
            }

            BottomActionBar(
                onEditTagClick = { onAction(ImageDetailAction.ShowSheet(Sheet.Edit)) },
                onDeleteClick = { onAction(ImageDetailAction.OnDeleteScreenshot) }
            )
        }


        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (state.currentSheet != Sheet.None) {
            ModalBottomSheet(
                onDismissRequest = {
                    when (state.currentSheet) {
                        Sheet.Add -> onAction(ImageDetailAction.ShowSheet(Sheet.Edit))
                        Sheet.Edit -> onAction(ImageDetailAction.HideSheet)
                        else -> onAction(ImageDetailAction.HideSheet)
                    }
                },
                dragHandle = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .padding(top = 8.dp)
                                .size(width = 40.dp, height = 4.dp)
                                .background(Gray05.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                        )
                    }
                },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = Color.White
            ) {
                when (state.currentSheet) {
                    Sheet.Edit -> TagEditContent(state, onAction)
                    Sheet.Add  -> TagAddContent(state, onAction)
                    else -> {}
                }
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
            contentDescription = stringResource(R.string.common_back),
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
                contentDescription = stringResource(R.string.image_detail_screenshot),
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

        Icon(
            painter = painterResource(
                id = if (isFavorite) R.drawable.ic_tab_favorite_checked else R.drawable.ic_tab_favorite_unchecked
            ),
            contentDescription = stringResource(R.string.favorite_icon),
            tint = Color.White,
            modifier = Modifier
                .clickable { onFavoriteToggle() }
                .size(24.dp)
        )
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
                    contentDescription = stringResource(R.string.image_detail_tag_edit),
                    tint = PureWhite,
                    modifier = Modifier.clickableWithoutRipple(
                        enabled = true,
                        onClick = onEditTagClick
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.image_detail_tag_edit),
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
                    contentDescription = stringResource(R.string.common_delete),
                    tint = PureWhite,
                    modifier = Modifier.clickableWithoutRipple(
                        enabled = true,
                        onClick = onDeleteClick
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.common_delete),
                    color = PureWhite,
                    style = caption02Regular
                )
            }
        }
    }
}

sealed interface Sheet { data object None: Sheet; data object Edit: Sheet; data object Add: Sheet }
