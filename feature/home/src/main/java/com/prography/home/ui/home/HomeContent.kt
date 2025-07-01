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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.tooling.preview.Preview
import com.prography.home.ui.home.component.FavoriteCardDeck
import com.prography.ui.R

@Composable
fun HomeContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
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
                screenshots = state.screenshots
            )
        }

        // Sticky Filter Chips
        stickyHeader {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.tags) { tag ->
                    UiTagChip(
                        text = tag,
                        isSelected = tag == state.selectedTag,
                        onClick = { onAction(HomeAction.SelectTag(tag)) }
                    )
                }
            }
        }

        // Grid → 2개씩 보여주기 (row 단위로 직접 배치)
        val filteredScreenshots = state.screenshots.filter {
            state.selectedTag == "전체" || it.appName == state.selectedTag
        }

        items(filteredScreenshots.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { screenshot ->
                    ScreenshotItem(
                        screenshot = screenshot,
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

@Composable
fun ScreenshotItem(
    screenshot: UiScreenshotModel,
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
    ) {
        Image(
            painter = rememberAsyncImagePainter(screenshot.uri),
            contentDescription = screenshot.appName,
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
