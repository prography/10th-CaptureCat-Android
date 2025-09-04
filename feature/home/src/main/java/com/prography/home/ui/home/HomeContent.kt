package com.prography.home.ui.home

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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.prography.ui.component.clickableWithoutRipple
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import com.prography.home.ui.home.component.ErrorReportBanner
import com.prography.ui.theme.Divider

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    pagingItems: LazyPagingItems<UiScreenshotModel>
) {
    // 초기 로딩 중인지 확인
    val isInitialLoading = pagingItems.loadState.refresh is LoadState.Loading

    // Debug logging
    LaunchedEffect(state.favoriteScreenshots) {
        Timber.d("HomeContent - State has ${state.favoriteScreenshots.size} favorite screenshots")
    }

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
                        info = stringResource(R.string.home_no_screenshot_info),
                        buttonText = stringResource(R.string.home_go_to_storage),
                        onClick = { onAction(HomeAction.NavigateToStorage) }
                    )
                }
            }

            else -> {
                // 스크린샷 그리드 리스트 (한 줄에 3개씩)
                val screenshotItems = (0 until pagingItems.itemCount).mapNotNull { index ->
                    pagingItems[index]
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
