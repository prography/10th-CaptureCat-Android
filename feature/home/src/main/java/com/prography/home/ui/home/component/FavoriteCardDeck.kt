package com.prography.home.ui.home.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.prography.domain.model.UiScreenshotModel
import com.prography.ui.theme.caption01SemiBold

@Composable
fun FavoriteCardDeck(
    screenshots: List<UiScreenshotModel>,
    modifier: Modifier = Modifier
) {
    val favoriteScreenshots = remember(screenshots) {
        screenshots.filter { it.isFavorite }
    }

    if (favoriteScreenshots.isNotEmpty()) {
        val pagerState = rememberPagerState(
            pageCount = { favoriteScreenshots.size },
            initialPage = 0
        )

        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 50.dp),
            key = { index -> favoriteScreenshots.getOrNull(index)?.id ?: index }
        ) { page ->
            val screenshot = favoriteScreenshots[page]

            val isCurrentPage = pagerState.currentPage == page

            val pageScale by animateFloatAsState(
                targetValue = if (isCurrentPage) 1f else 0.8f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                ),
                label = "pageScale"
            )

            val pageAlpha by animateFloatAsState(
                targetValue = if (isCurrentPage) 1f else 0.5f,
                animationSpec = tween(300),
                label = "pageAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .graphicsLayer {
                        scaleY = pageScale
                        alpha = pageAlpha
                    }
                    .border(
                        width = 1.dp,
                        color = Color(0x0D001758),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(screenshot.uri),
                    contentDescription = screenshot.appName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                FlowRow(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    screenshot.tags.forEach { tag ->
                        Text(
                            text = tag,
                            style = caption01SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0x66000000), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
} 