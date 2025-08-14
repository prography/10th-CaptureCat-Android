package com.prography.home.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import com.prography.ui.theme.caption01SemiBold
import com.prography.ui.component.UiEmptyState
import coil3.compose.AsyncImage
import com.prography.home.ui.home.component.FavoriteCardDeck
import com.prography.ui.R
import com.prography.ui.theme.Primary
import timber.log.Timber
import androidx.compose.foundation.layout.FlowRow
import com.prography.ui.component.clickableWithoutRipple
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import com.prography.home.ui.home.component.ErrorReportBanner

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
                contentDescription = "로고"
            )
            Image(
                painter = painterResource(id = R.drawable.ic_search_bar_icon),
                contentDescription = "검색",
                modifier = Modifier.clickableWithoutRipple {
                    onAction(HomeAction.NavigateToSearch)
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
                        title = "아직 스크린샷이 없어요.",
                        info = "임시보관함에서 스크린샷을 저장할 수 있어요!",
                        buttonText = "임시보관함 가기",
                        onClick = { onAction(HomeAction.NavigateToStorage) }
                    )
                }
            }

            else -> {
                // 스크린샷 리스트
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // 즐겨찾기 카드 덱
                    item {
                        FavoriteCardDeck(
                            favoriteScreenshots = state.favoriteScreenshots,
                            onFavoriteClick = { onAction(HomeAction.NavigateToFavorite) }
                        )
                    }

                    // Paging된 스크린샷들을 2개씩 묶어서 표시
                    val screenshots = (0 until pagingItems.itemCount).mapNotNull { index ->
                        pagingItems[index]
                    }

                    items(count = screenshots.size / 2 + screenshots.size % 2) { rowIndex ->
                        val startIndex = rowIndex * 2
                        val endIndex = minOf(startIndex + 2, screenshots.size)
                        val rowItems = screenshots.subList(startIndex, endIndex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { screenshot ->
                                ScreenshotItem(
                                    screenshot = screenshot,
                                    onScreenshotClick = {
                                        onAction(
                                            HomeAction.OnScreenshotClick(
                                                screenshot
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 로딩 인디케이터 (Paging3가 append 시 자동 처리)
                    when (pagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item {
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
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "더 불러오기 실패",
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
                width = 0.75.dp, color = Color(0x0D001758),
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 9.dp)
        ) {
            screenshot.tags.forEach { tag ->
                Text(
                    text = tag.name,
                    style = caption01SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0x66000000), RoundedCornerShape(4.5.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
