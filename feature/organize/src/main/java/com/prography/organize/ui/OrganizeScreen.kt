package com.prography.organize.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizeScreen(
    screenshots: List<OrganizeScreenshotItem>,
    currentIndex: Int = 0,
    onNavigateUp: () -> Unit,
    onComplete: () -> Unit
) {
    var items by remember { mutableStateOf(screenshots) }
    val pagerState = rememberPagerState(initialPage = currentIndex) { items.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(screenshots) {
        if (items != screenshots && screenshots.isNotEmpty()) {
            items = screenshots
        }
    }

    LaunchedEffect(currentIndex) {
        if (items.isNotEmpty()) {
            coroutineScope.launch {
                pagerState.scrollToPage(currentIndex)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        OrganizeTopBar(
            currentIndex = if (items.isNotEmpty()) pagerState.currentPage + 1 else 0,
            totalCount = items.size,
            onNavigateUp = onNavigateUp,
            onComplete = onComplete
        )

        // Image Pager
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (items.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { index -> items.getOrNull(index)?.id ?: index },
                    pageSpacing = 16.dp,
                    contentPadding = PaddingValues(horizontal = 32.dp)
                ) { page ->
                    items.getOrNull(page)?.let { screenshot ->
                        OrganizeImageCard(
                            screenshot = screenshot,
                            isCurrentPage = page == pagerState.currentPage,
                            onFavoriteToggle = { isFavorite ->
                                items = items.toMutableList().apply {
                                    val index = indexOfFirst { it.id == screenshot.id }
                                    if (index != -1) {
                                        this[index] = this[index].copy(isFavorite = isFavorite)
                                    }
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    val currentPage = pagerState.currentPage
                                    val newItems = items.toMutableList().apply {
                                        removeAt(currentPage)
                                    }

                                    if (newItems.isEmpty()) {
                                        onComplete()
                                    } else {
                                        // 먼저 아이템 리스트 업데이트
                                        items = newItems

                                        // 다음 페이지 계산
                                        val nextPage = when {
                                            currentPage >= newItems.size -> newItems.size - 1
                                            else -> currentPage
                                        }.coerceAtLeast(0)

                                        // 페이지 이동 (즉시)
                                        if (nextPage != currentPage) {
                                            pagerState.scrollToPage(nextPage)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                // 모든 사진이 삭제된 경우
                CompletionMessage(onComplete = onComplete)
            }
        }

        // Bottom Controls (즐겨찾기, 미분류, 태그 추가)
        if (items.isNotEmpty()) {
            OrganizeBottomControls(
                screenshot = items.getOrNull(pagerState.currentPage),
                onFavoriteToggle = { isFavorite ->
                    val currentPage = pagerState.currentPage
                    val currentScreenshot = items.getOrNull(currentPage)
                    currentScreenshot?.let { screenshot ->
                        items = items.toMutableList().apply {
                            val index = indexOfFirst { it.id == screenshot.id }
                            if (index != -1) {
                                this[index] = this[index].copy(isFavorite = isFavorite)
                            }
                        }
                    }
                }
            )
        }
    }
}
