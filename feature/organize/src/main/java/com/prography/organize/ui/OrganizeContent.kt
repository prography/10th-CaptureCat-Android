package com.prography.organize.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prography.domain.model.TagModel
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.contract.OrganizeState
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.UiAddTagChip
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Gray05
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.subhead01Bold

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
        )


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

        Spacer(modifier = Modifier.height(30.dp))

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
                    }
                }

                OrganizeMode.SINGLE -> {
                    if (state.screenshots.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { index -> state.screenshots.getOrNull(index)?.id ?: index },
                            pageSpacing = 12.dp,
                            contentPadding = PaddingValues(horizontal = 77.dp)
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
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(37.dp))

        // 없을 경우 추가하기 + 버튼

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp) // 한 줄 높이
                .padding(end = 16.dp, bottom = 16.dp)
                .horizontalScroll(rememberScrollState())
        ) {

            val screenshotId = getCurrentScreenshotId()
            val selectedTags: List<String> = getCurrentScreenshotTags()
            val count = selectedTags.size

            selectedTags.forEach { name ->
                UiTagSelectedChip(
                    text = name,
                    onDelete = { onAction(OrganizeAction.OnTagToggle(screenshotId, name)) }
                )
            }
            if (count < 4) {
                val addLabel = if (count == 0) "추가하기 +" else "+"

                UiAddTagChip(
                    label = addLabel,
                    onClick = { onAction(OrganizeAction.OnAddTag(screenshotId)) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            OrganizeBottomControls(
                availableTags = state.availableTags.map { it.name },
                selectedTags = getCurrentScreenshotTags(),
                onTagToggle = { tagText ->
                    val screenshotId = getCurrentScreenshotId()
                    onAction(OrganizeAction.OnTagToggle(screenshotId, tagText))
                }
            )
        }

        UiLabelAddButton(
            onClick = { onAction(OrganizeAction.OnSaveScreenshots) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            text = "저장하기",
            size = ButtonSize.LARGE
        )
    }
}

@Composable
fun OrganizeStackedCards(
    screenshots: List<OrganizeScreenshotItem>
) {
    val density = LocalDensity.current
    val screenshot = screenshots.firstOrNull() ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 뒤 배경용 그라디언트 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 77.dp)
                .aspectRatio(0.65f)
                .graphicsLayer {
                    translationX = with(density) { 1.dp.toPx() }
                    translationY = with(density) { 1.dp.toPx() }
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 0.96f
                    rotationZ = -4f
                },
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray04)
                    .border(
                        width = 1.dp,
                        color = com.prography.ui.theme.Divider,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        // 실제 이미지 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 77.dp)
                .aspectRatio(0.65f),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = screenshot.uri,
                    contentDescription = stringResource(com.prography.ui.R.string.organize_screenshot),
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
    val tabs = listOf(
        OrganizeMode.BATCH to stringResource(com.prography.ui.R.string.organize_mode_batch),
        OrganizeMode.SINGLE to stringResource(com.prography.ui.R.string.organize_mode_single)
    )
    val selectedIndex = tabs.indexOfFirst { it.first == currentMode }.coerceAtLeast(0)

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            divider = {},
            indicator = { positions ->
                val pos = positions[selectedIndex]
                Box(
                    Modifier
                        .tabIndicatorOffset(pos)
                        .fillMaxWidth()
                        .padding()
                        .height(3.dp)
                        .background(Primary, RoundedCornerShape(1.5.dp))
                )
            }
        ) {
            tabs.forEachIndexed { index, (mode, label) ->
                val selected = index == selectedIndex
                val color by animateColorAsState(
                    targetValue = if (selected) Primary else Text03,
                    label = "tabColor"
                )

                val interactionSource = remember { MutableInteractionSource() }

                Tab(
                    selected = selected,
                    onClick = { onModeChange(mode) },
                    selectedContentColor = Primary,
                    unselectedContentColor = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .indication(interactionSource, null),
                    interactionSource = interactionSource
                ) {
                    Box(Modifier.padding(vertical = 10.dp)) {
                        Text(
                            text = label,
                            style = subhead01Bold,
                            color = color
                        )
                    }
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = com.prography.ui.theme.Divider)
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
        availableTags = listOf(
            TagModel(0, "쇼핑"),
            TagModel(0, "여행"),
            TagModel(0, "음식")
        ),
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