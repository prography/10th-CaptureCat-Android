package com.prography.home.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import com.prography.ui.theme.caption01SemiBold
import com.prography.ui.component.UiEmptyState
import coil3.compose.AsyncImage
import com.prography.ui.R
import com.prography.ui.theme.Primary
import timber.log.Timber
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.prography.ui.component.clickableWithoutRipple
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import com.prography.home.ui.home.component.ErrorReportBanner
import com.prography.home.ui.home.component.FabShadowLayers
import com.prography.home.ui.home.component.multiShadow
import com.prography.ui.theme.Divider
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.subhead01Bold

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    pagingItems: LazyPagingItems<UiScreenshotModel>
) {
    val allTabLabel = stringResource(R.string.label_all)
    // 초기 로딩 중인지 확인
    val isInitialLoading = pagingItems.loadState.refresh is LoadState.Loading


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // 공통 Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_header_logo),
                contentDescription = stringResource(R.string.common_logo)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = stringResource(R.string.tab_mypage),
                modifier = Modifier.clickableWithoutRipple {
                    onAction(HomeAction.NavigateToMyPage)
                }
            )
        }

        // 공통 오류 제보 배너
        if (state.showErrorReportBanner) {
            ErrorReportBanner(
                onReportClick = { onAction(HomeAction.OnErrorReportClick) }
            )
        }

        // 탭 세션
        TabSection(
            popularTags = state.popularTags,
            selectedTab = state.selectedTab,
            onTabSelected = { tabTag ->
                onAction(HomeAction.OnTabSelected(tabTag))
            },
            onTagSettingClick = { onAction(HomeAction.NavigateToTagSetting) }
        )

        // 본문: 상황에 따라 다른 영역
        when {
            // 초기 로딩 중 (처음 진입 시)
            isInitialLoading && pagingItems.itemCount == 0 -> {
                // 필요 시 로딩 표시를 추가할 수 있음. 현재는 헤더/배너만 표시.
                Spacer(modifier = Modifier.weight(1f))
            }

            // 스크린샷이 없을 때 EmptyState 표시
            pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    UiEmptyState(
                        title = stringResource(R.string.home_no_screenshot_title),
                        info = stringResource(R.string.home_no_screenshot_info)
                    )
                }
            }

            else -> {
                // 스크린샷 그리드 리스트 (한 줄에 3개씩)
                if (state.selectedTab == allTabLabel) {
                    // "전체" 탭 선택 시: Paging3 사용
                    val screenshotItems = (0 until pagingItems.itemCount).mapNotNull { index ->
                        pagingItems[index]
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(screenshotItems) { screenshot ->
                            ScreenshotItem(
                                screenshot = screenshot,
                                onScreenshotClick = {
                                    onAction(HomeAction.OnScreenshotClick(screenshot))
                                },
                                modifier = Modifier
                            )
                        }

                        // 로딩 인디케이터 (Paging3가 append 시 자동 처리)
                        when (pagingItems.loadState.append) {
                            is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Primary
                                        )
                                    }
                                }
                            }

                            is LoadState.Error -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.home_loading_failed),
                                            color = Color.Red,
                                            modifier = Modifier.clickable { pagingItems.retry() }
                                        )
                                    }
                                }
                            }

                            else -> { /* no-op */
                            }
                        }
                    }
                } else {
                    // 특정 태그 선택 시: state.screenshots 사용
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.screenshots) { screenshot ->
                            ScreenshotItem(
                                screenshot = screenshot,
                                onScreenshotClick = {
                                    onAction(HomeAction.OnScreenshotClick(screenshot))
                                },
                                modifier = Modifier
                            )
                        }

                        // 태그별 검색 로딩 표시
                        if (state.isLoadingTags) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenshotItem(
    screenshot: UiScreenshotModel,
    onScreenshotClick: (UiScreenshotModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(45f / 76f)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 0.75.dp, color = Divider,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .clickable { onScreenshotClick(screenshot) }
    ) {
        Timber.d("ScreenshotItem: $screenshot.uri")
        AsyncImage(
            model = screenshot.uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun TabSection(
    popularTags: List<com.prography.domain.model.TagWithCount>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onTagSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTabLabel = stringResource(R.string.label_all)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            // 스크롤 가능한 탭 영역
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "전체" 탭
                item {
                    TagChip(
                        text = allTabLabel,
                        isSelected = selectedTab == allTabLabel,
                        onClick = { onTabSelected(allTabLabel) }
                    )
                }

                // 인기 태그들
                items(popularTags) { tag ->
                    TagChip(
                        text = tag.tag,
                        isSelected = selectedTab == tag.tag,
                        onClick = { onTabSelected(tag.tag) }
                    )
                }
            }
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

        // 세로 구분선
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 6.dp)
                .width(1.dp)
                .height(10.dp)
                .background(com.prography.ui.theme.Gray03)
        )

        // 고정된 태그 설정 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_tab_section),
            contentDescription = "태그 설정",
            tint = Color.Unspecified,
            modifier = Modifier
                .clickableWithoutRipple { onTagSettingClick() }
        )
    }
}

@Composable
fun TagChip(
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
            style = subhead01Bold,
            color = if (isSelected) Color(0xFFF05F00) else Text03,
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = if (isSelected) Color(0xFFF05F00) else Color.Transparent,
            thickness = 3.dp
        )
    }
}

@Composable
fun CaptureCatFab(
    onUploadClick: () -> Unit,
    onOrganizeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "rotation"
    )
    val bgColor by animateColorAsState(
        targetValue = if (expanded) Color.White else Color(0xFFFF6600),
        animationSpec = tween(250),
        label = "bgColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (expanded) Color(0xFFFF6600) else Color.White,
        animationSpec = tween(250),
        label = "iconColor"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 🔹 배경 dim
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickableWithoutRipple { expanded = false }
            )
        }

        // 🔹 메뉴 (한 박스로)
        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 82.dp)
                    .shadow(
                        elevation = 6.dp,
                        spotColor = Color(0x1A000000),
                        ambientColor = Color(0x1A000000),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .width(IntrinsicSize.Max)
                    .padding(top = 12.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FabMenuItem(
                    iconRes = R.drawable.ic_fab_upload,
                    text = stringResource(com.prography.ui.R.string.fab_upload),
                    onClick = {
                        expanded = false
                        onUploadClick()
                    }
                )
                FabMenuItem(
                    iconRes = R.drawable.ic_fab_delete,
                    text = stringResource(com.prography.ui.R.string.fab_organize),
                    onClick = {
                        expanded = false
                        onOrganizeClick()
                    }
                )
            }
        }

        // 🔹 메인 FAB
        Box(
            modifier = Modifier
                .padding(end = 20.dp, bottom = 20.dp)
                .size(50.dp)
                .multiShadow(layers = FabShadowLayers, radius = 25.dp)
                .background(bgColor, RoundedCornerShape(25.dp))
                .clickableWithoutRipple { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fab_button),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
        }
    }
}

@Composable
private fun FabMenuItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .widthIn(min = 139.dp)
                .clickableWithoutRipple { onClick() }
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Text02,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(15.dp))
            Text(
                text = text,
                color = Text02,
                style = subhead01Bold
            )
        }
    }
}