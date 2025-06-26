package com.prography.organize.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.subhead01Bold
import com.prography.ui.theme.subhead02Bold
import kotlinx.coroutines.launch

enum class OrganizeMode {
    BATCH,      // 한번에
    SINGLE      // 한장씩
}

@Composable
fun TagChip(
    text: String,
    isSelected: Boolean,
    isAddButton: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = Gray04,
                        shape = RoundedCornerShape(20.dp)
                    )
                } else {
                    Modifier
                }
            )
            .background(
                color = when {
                    isSelected -> Primary
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = when {
                isSelected -> subhead02Bold
                else -> body02Regular
            },
            color = when {
                isSelected -> Color.White
                else -> Color.Black
            },
            maxLines = 1, // 한 줄로 제한
            overflow = TextOverflow.Ellipsis // 긴 텍스트는 ... 처리
        )
    }
}

@Composable
fun OrganizeStackedCards(
    screenshots: List<OrganizeScreenshotItem>,
    onFavoriteToggle: (String, Boolean) -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 34.dp, horizontal = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        // 2장까지만 스택으로 보여줌 (뒤에서부터 그리기)
        val visibleItems = screenshots.take(2)

        // 뒤에서부터 그려야 맨 앞이 위에 보임
        visibleItems.reversed().forEachIndexed { reverseIndex, screenshot ->
            val index = visibleItems.size - 1 - reverseIndex // 실제 인덱스

            // 뒤로 갈수록 더 많이 오프셋과 회전
            val offsetX = (index * 1).dp  // X축 오프셋
            val offsetY = (index * 1).dp   // Y축 오프셋
            val scale = 1f - (index * 0.00f)  // 크기 변화 (더 작게)
            val cardAlpha = 1f - (index * 0.08f)  // 투명도 (더 작게)
            val rotation = index * -8f  // 뒤로 갈수록 왼쪽으로 10도씩 회전

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .graphicsLayer {
                        translationX = with(density) { offsetX.toPx() }
                        translationY = with(density) { offsetY.toPx() }
                        scaleX = scale
                        scaleY = scale
                        alpha = cardAlpha
                        rotationZ = rotation  // Z축 회전 (평면 회전)
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                    },
                shape = RoundedCornerShape(26.dp),
                elevation = CardDefaults.cardElevation((1 + index * 2).dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 이미지
                    AsyncImage(
                        model = screenshot.uri,
                        contentDescription = "스크린샷",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 한번에 모드에서는 즐겨찾기 버튼 없음
                    // (한장씩 모드에서만 표시)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizeScreen(
    screenshots: List<OrganizeScreenshotItem>,
    currentIndex: Int = 0,
    onNavigateUp: () -> Unit,
    onComplete: () -> Unit
) {
    var items by remember { mutableStateOf(screenshots) }
    var organizeMode by remember { mutableStateOf(OrganizeMode.BATCH) } // 디폴트: 한번에
    val pagerState = rememberPagerState(initialPage = currentIndex) { items.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(screenshots) {
        if (items != screenshots && screenshots.isNotEmpty()) {
            items = screenshots
        }
    }

    LaunchedEffect(currentIndex) {
        if (items.isNotEmpty() && organizeMode == OrganizeMode.SINGLE) {
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
        // Top Bar with Mode Toggle
        OrganizeTopBar(
            currentIndex = if (organizeMode == OrganizeMode.SINGLE && items.isNotEmpty())
                pagerState.currentPage + 1 else 0,
            totalCount = items.size,
            onNavigateUp = onNavigateUp,
            onComplete = onComplete
        )

        // Mode Toggle Buttons (임시)
        Box(
            modifier = Modifier.fillMaxWidth(), // 상위 너비는 꽉 채우고
            contentAlignment = Alignment.Center // 그 안에서 Row를 중앙 정렬
        ) {
            Row(
                modifier = Modifier
                    .background(color = Gray03, shape = RoundedCornerShape(size = 9.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { organizeMode = OrganizeMode.BATCH },
                    colors = ButtonColors(containerColor = Color.White, contentColor = Text01,
                        disabledContainerColor = Color.Transparent, disabledContentColor = Text01),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = null
                ) {
                    Text(text= "한번에",style = subhead02Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { organizeMode = OrganizeMode.SINGLE },
                    colors = if (organizeMode == OrganizeMode.SINGLE)
                        ButtonDefaults.buttonColors()
                    else
                        ButtonDefaults.outlinedButtonColors(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = null
                ) {
                    Text("한장씩", style = subhead02Bold)
                }
            }
        }

        // Content based on mode
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (organizeMode) {
                OrganizeMode.BATCH -> {
                    // 한번에 모드: 간단한 하단 컨트롤
                    if (items.isNotEmpty()) {
                        OrganizeStackedCards(
                            screenshots = items,
                            onFavoriteToggle = { id, isFavorite ->
                                items = items.toMutableList().apply {
                                    val index = indexOfFirst { it.id == id }
                                    if (index != -1) {
                                        this[index] =
                                            this[index].copy(isFavorite = isFavorite)
                                    }
                                }
                            }
                        )
                    } else {
                        CompletionMessage(onComplete = onComplete)
                    }
                }

                OrganizeMode.SINGLE -> {
                    // 한장씩 모드: 기존 HorizontalPager
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
                                        val currentPage = pagerState.currentPage
                                        val currentScreenshot = items.getOrNull(currentPage)
                                        currentScreenshot?.let { screenshot ->
                                            items = items.toMutableList().apply {
                                                val index = indexOfFirst { it.id == screenshot.id }
                                                if (index != -1) {
                                                    this[index] =
                                                        this[index].copy(isFavorite = isFavorite)
                                                }
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
            }
        }
        OrganizeBottomControls()
    }
}

@Composable
fun CompletionMessage(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("모든 스크린샷 정리가 완료되었습니다!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onComplete) {
            Text("완료")
        }
    }
}

// Preview 함수들
@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun OrganizeScreenFullPreview() {
    val mockScreenshots = listOf(
        OrganizeScreenshotItem(
            id = "1",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_1.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "2",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_2.png",
            isFavorite = true
        ),
        OrganizeScreenshotItem(
            id = "3",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_3.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "4",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_4.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "5",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_5.png",
            isFavorite = true
        )
    )

    OrganizeScreen(
        screenshots = mockScreenshots,
        currentIndex = 0,
        onNavigateUp = { },
        onComplete = { }
    )
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "한장씩 모드")
@Composable
fun OrganizeScreenSingleModePreview() {
    val mockScreenshots = listOf(
        OrganizeScreenshotItem(
            id = "1",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_1.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "2",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_2.png",
            isFavorite = true
        )
    )

    // 한장씩 모드로 시작하는 버전
    var organizeMode by remember { mutableStateOf(OrganizeMode.SINGLE) }

    OrganizeScreen(
        screenshots = mockScreenshots,
        currentIndex = 0,
        onNavigateUp = { },
        onComplete = { }
    )
}

@Preview(showBackground = true)
@Composable
fun OrganizeStackedCardsPreview() {
    val mockScreenshots = listOf(
        OrganizeScreenshotItem(
            id = "1",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_1.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "2",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_2.png",
            isFavorite = true
        ),
        OrganizeScreenshotItem(
            id = "3",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_3.png",
            isFavorite = false
        )
    )

    OrganizeStackedCards(
        screenshots = mockScreenshots,
        onFavoriteToggle = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun OrganizeBatchBottomControlsPreview() {
    OrganizeBottomControls()
}

@Preview(showBackground = true, heightDp = 80)
@Composable
fun TagChipPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        TagChip(
            text = "소원",
            isSelected = true,
            onClick = { }
        )
        TagChip(
            text = "여행",
            isSelected = false,
            onClick = { }
        )
        TagChip(
            text = "태그 추가 +",
            isSelected = false,
            isAddButton = true,
            onClick = { }
        )
    }
}
