package com.prography.home.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import coil3.compose.rememberAsyncImagePainter
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.home.ui.home.contract.HomeState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import com.prography.ui.theme.caption01SemiBold
import com.prography.ui.component.UiTagChip
import com.prography.ui.component.UiEmptyState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.prography.home.ui.home.component.FavoriteCardDeck
import com.prography.ui.R
import timber.log.Timber

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    // 스크린샷이 아예 없을 때와 필터링 후 없을 때를 구분
    val hasAnyScreenshots = state.screenshots.isNotEmpty()
    val filteredScreenshots = state.screenshots.filter {
        state.selectedTag == "전체"
    }

    if (!hasAnyScreenshots) {
        // 스크린샷이 아예 없을 때는 헤더와 빈 상태만 표시
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header만 표시
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_header_logo),
                    contentDescription = "로고"
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "프로필 아이콘",
                    modifier = Modifier.clickable {
                        onAction(HomeAction.NavigateToSettings)
                    }
                )
            }

            // 중앙 정렬된 빈 상태
            Box(
                modifier = Modifier
                    .fillMaxSize(),
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
    } else {
        // 기존 레이아웃 (스크린샷이 있을 때)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_header_logo),
                        contentDescription = "로고"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "프로필 아이콘",
                        modifier = Modifier.clickable {
                            onAction(HomeAction.NavigateToSettings)
                        }
                    )
                }
            }

            item {
                FavoriteCardDeck(
                    screenshots = state.screenshots,
                    onFavoriteClick = { onAction(HomeAction.NavigateToFavorite) }
                )
            }

            // 필터링 후 결과가 없을 때
            if (filteredScreenshots.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // 적당한 높이 지정
                        contentAlignment = Alignment.Center
                    ) {
                        UiEmptyState(
                            title = "선택한 태그의 스크린샷이 없어요.",
                            info = "다른 태그를 선택해보세요!",
                            buttonText = "전체 보기",
                            onClick = { onAction(HomeAction.SelectTag("전체")) }
                        )
                    }
                }
            } else {
                items(filteredScreenshots.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 9.dp)
        ) {
            screenshot.tags.forEach { tag ->
                Text(
                    text = tag,
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
