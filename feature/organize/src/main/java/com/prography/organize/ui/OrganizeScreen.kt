package com.prography.organize.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.organize.ui.viewmodel.OrganizeViewModel
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body02Regular
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
    val screenshot = screenshots.firstOrNull() ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 34.dp),
        contentAlignment = Alignment.Center
    ) {
        // 🔹 뒤 배경용 그라디언트 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .aspectRatio(0.65f)
                .graphicsLayer {
                    translationX = with(density) { 1.dp.toPx() }
                    translationY = with(density) { 1.dp.toPx() }
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 0.92f
                    rotationZ = -8f
                },
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFA5AEBF),
                                Color(0xFF4D5159)
                            )
                        )
                    )
            )
        }

        // 🔸 실제 이미지 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .aspectRatio(0.65f),
            shape = RoundedCornerShape(26.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = screenshot.uri,
                    contentDescription = "스크린샷",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
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
    onComplete: () -> Unit,
    viewModel: OrganizeViewModel = hiltViewModel()
) {

    val tagItems by viewModel.tagItems.collectAsState()
    var items by remember { mutableStateOf(screenshots) }
    var organizeMode by remember { mutableStateOf(OrganizeMode.BATCH) } // 디폴트: 한번에
    val pagerState = rememberPagerState(initialPage = currentIndex) { items.size }
    val coroutineScope = rememberCoroutineScope()

    var showAddTagBottomSheet by remember { mutableStateOf(false) }

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
        OrganizeTopBar(
            currentIndex = if (organizeMode == OrganizeMode.SINGLE && items.isNotEmpty())
                pagerState.currentPage + 1 else 0,
            totalCount = items.size,
            onNavigateUp = onNavigateUp,
            onComplete = onComplete
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OrganizeModeToggle(
                currentMode = organizeMode,
                onModeChange = { newMode -> organizeMode = newMode }
            )
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
                            contentPadding = PaddingValues(horizontal = 60.dp)
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
        OrganizeBottomControls(
            tags = tagItems,
            onTagToggle = viewModel::toggleTag,
            onAddTag = { showAddTagBottomSheet = true }
        )
    }
    if (showAddTagBottomSheet) {
        TagAddBottomSheet(
            onAdd = { tagName ->
                viewModel.addTag(tagName)
                showAddTagBottomSheet = false
            },
            onDismiss = { showAddTagBottomSheet = false }
        )
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

@Composable
fun OrganizeModeToggle(
    currentMode: OrganizeMode,
    onModeChange: (OrganizeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Gray03,
                shape = RoundedCornerShape(9.dp)
            )
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // 한번에 버튼
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onModeChange(OrganizeMode.BATCH) }
                .then(
                    if (currentMode == OrganizeMode.BATCH) {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(7.dp),
                            spotColor = Color.Black.copy(0.25f)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(
                    color = if (currentMode == OrganizeMode.BATCH) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 34.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "한번에",
                style = if (currentMode == OrganizeMode.BATCH) subhead02Bold else body02Regular ,
                color = if (currentMode == OrganizeMode.BATCH) Text01 else Color.Gray
            )
        }

        // 한장씩 버튼
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onModeChange(OrganizeMode.SINGLE) }
                .then(
                    if (currentMode == OrganizeMode.SINGLE) {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(7.dp),
                            spotColor = Color.Black.copy(0.25f)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(
                    color = if (currentMode == OrganizeMode.SINGLE) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 34.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "한장씩",
                style = if (currentMode == OrganizeMode.SINGLE) subhead02Bold else body02Regular ,
                color = if (currentMode == OrganizeMode.SINGLE) Text01 else Color.Gray
            )
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

    // 한장씩 모드로 시작하는 커스텀 화면
    var organizeMode by remember { mutableStateOf(OrganizeMode.SINGLE) }
    var items by remember { mutableStateOf(mockScreenshots) }
    val pagerState = rememberPagerState(initialPage = 0) { items.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        OrganizeTopBar(
            currentIndex = if (items.isNotEmpty()) pagerState.currentPage + 1 else 0,
            totalCount = items.size,
            onNavigateUp = { },
            onComplete = { }
        )

        // Mode Toggle (한장씩 선택된 상태)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OrganizeModeToggle(
                currentMode = organizeMode,
                onModeChange = {
                    organizeMode = it
                }
            )
        }

        // 한장씩 모드 콘텐츠
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { index -> items.getOrNull(index)?.id ?: index },
                pageSpacing = 12.dp,
                contentPadding = PaddingValues(horizontal = 60.dp)
            ) { page ->
                items.getOrNull(page)?.let { screenshot ->
                    OrganizeImageCard(
                        screenshot = screenshot,
                        isCurrentPage = page == pagerState.currentPage,
                        onFavoriteToggle = { _ -> },
                        onDelete = { }
                    )
                }
            }
        }

        // 하단 태그 영역
        OrganizeBottomControls()
    }
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
