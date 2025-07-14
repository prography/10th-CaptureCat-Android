package com.prography.organize.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.contract.OrganizeState
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.subhead02Bold

@Composable
fun OrganizeContent(
    state: OrganizeState,
    pagerState: PagerState,
    onAction: (OrganizeAction) -> Unit,
    getCurrentScreenshotTags: () -> List<String>,
    getCurrentScreenshotId: () -> String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OrganizeTopBar(
            currentIndex = if (state.organizeMode == OrganizeMode.SINGLE && state.screenshots.isNotEmpty())
                state.currentIndex + 1 else 0,
            totalCount = state.screenshots.size,
            onNavigateUp = { onAction(OrganizeAction.OnNavigateUp) },
            onComplete = { onAction(OrganizeAction.OnSaveScreenshots) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OrganizeModeToggle(
                currentMode = state.organizeMode,
                onModeChange = { newMode ->
                    onAction(OrganizeAction.OnModeChange(newMode))
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (state.organizeMode) {
                OrganizeMode.BATCH -> {
                    if (state.screenshots.isNotEmpty()) {
                        OrganizeStackedCards(
                            screenshots = state.screenshots
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                OrganizeMode.SINGLE -> {
                    if (state.screenshots.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { index -> state.screenshots.getOrNull(index)?.id ?: index },
                            pageSpacing = 16.dp,
                            contentPadding = PaddingValues(horizontal = 50.dp)
                        ) { page ->
                            state.screenshots.getOrNull(page)?.let { screenshot ->
                                OrganizeImageCard(
                                    screenshot = screenshot,
                                    isCurrentPage = page == pagerState.currentPage,
                                    onFavoriteToggle = { isFavorite ->
                                        onAction(
                                            OrganizeAction.OnFavoriteToggle(
                                                screenshot.id,
                                                isFavorite
                                            )
                                        )
                                    },
                                    onDelete = {
                                        onAction(OrganizeAction.OnScreenshotDelete(screenshot.id))
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(66.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            OrganizeBottomControls(
                availableTags = state.availableTags,
                selectedTags = getCurrentScreenshotTags(),
                onTagToggle = { tagText ->
                    val screenshotId = getCurrentScreenshotId()
                    onAction(OrganizeAction.OnTagToggle(screenshotId, tagText))
                },
                onAddTag = {
                    val screenshotId = getCurrentScreenshotId()
                    onAction(OrganizeAction.OnAddTag(screenshotId))
                }
            )
        }
    }
}

@Composable
fun OrganizeStackedCards(
    screenshots: List<OrganizeScreenshotItem>
) {
    val density = LocalDensity.current
    val screenshot = screenshots.firstOrNull() ?: return

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(top = 18.dp)
    ) {
        // 뒤 배경용 그라디언트 카드
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

        // 실제 이미지 카드
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
                style = if (currentMode == OrganizeMode.BATCH) subhead02Bold else body02Regular,
                color = if (currentMode == OrganizeMode.BATCH) Text01 else Color.Gray
            )
        }

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
                style = if (currentMode == OrganizeMode.SINGLE) subhead02Bold else body02Regular,
                color = if (currentMode == OrganizeMode.SINGLE) Text01 else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun OrganizeContentPreview() {
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

    val mockState = OrganizeState(
        screenshots = mockScreenshots,
        currentIndex = 0,
        organizeMode = OrganizeMode.BATCH,
        availableTags = listOf("쇼핑", "여행", "음식"),
        isLoading = false
    )

    OrganizeContent(
        state = mockState,
        pagerState = androidx.compose.foundation.pager.rememberPagerState { mockScreenshots.size },
        onAction = {},
        getCurrentScreenshotTags = { emptyList() },
        getCurrentScreenshotId = { "" }
    )
}