package com.prography.favorite.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.prography.favorite.ui.contract.FavoriteAction
import com.prography.favorite.ui.contract.FavoriteState
import com.prography.domain.model.UiScreenshotModel
import com.prography.ui.component.UiTagInfoChip
import com.prography.ui.theme.*

@Composable
fun FavoriteContent(
    state: FavoriteState,
    onAction: (FavoriteAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_backward),
                contentDescription = "뒤로가기",
                modifier = Modifier.clickable { onAction(FavoriteAction.OnNavigateUp) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "즐겨찾기",
                style = headline02Bold,
                color = Text01
            )
        }

        when {
            state.isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("로딩 중...")
                }
            }

            !state.hasData -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "즐겨찾기한 스크린샷이 없어요",
                            style = headline02Bold,
                            color = Text02,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "마음에 드는 스크린샷을 즐겨찾기해보세요!",
                            style = body01Regular,
                            color = Text03,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                // Grid content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 2개씩 묶어서 그리드로 표시
                    val chunkedScreenshots = state.favoriteScreenshots.chunked(2)
                    items(chunkedScreenshots) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { screenshot ->
                                FavoriteScreenshotItem(
                                    screenshot = screenshot,
                                    onScreenshotClick = {
                                        onAction(
                                            FavoriteAction.OnScreenshotClick(
                                                screenshot
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // 한 개만 있는 경우 빈 공간 추가
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteScreenshotItem(
    screenshot: UiScreenshotModel,
    onScreenshotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(45f / 76f)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 0.75.dp,
                color = Gray02,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onScreenshotClick() }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray01),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(screenshot.uri),
                contentDescription = null ,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 즐겨찾기 아이콘 (우상단)
        Icon(
            painter = painterResource(id = com.prography.ui.R.drawable.ic_favorite_checked),
            contentDescription = "즐겨찾기",
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteContentPreview() {
    FavoriteContent(
        state = FavoriteState(
            favoriteScreenshots = emptyList(),
            hasData = false
        ),
        onAction = {}
    )
}