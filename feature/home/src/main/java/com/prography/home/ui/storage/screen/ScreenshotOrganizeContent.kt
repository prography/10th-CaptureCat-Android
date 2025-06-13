package com.prography.home.ui.storage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.contract.ScreenshotState
import com.prography.ui.R
import com.prography.ui.component.DeleteConfirmDialog

@Composable
fun ScreenshotOrganizeContent(
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenshotTopBar(
            isSelectionMode = state.isSelectionMode,
            selectedCount = state.selectedCount,
            totalCount = state.groupedScreenshots.values.flatten().size,
            isAllSelected = state.isAllSelected,
            onAction = onAction
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            state.groupedScreenshots.onEachIndexed { index, (date, screenshots) ->
                // 날짜 헤더 (전체 너비 차지)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = if (index == 0) 16.dp else 32.dp, // 첫 번째가 아니면 32dp로 간격 추가
                            end = 0.dp,
                            bottom = 7.dp
                        ),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily(Font(R.font.prography_pretendard_semibold))
                    )
                }

                // 스크린샷 아이템들
                items(screenshots) { screenshot ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { onAction(ScreenshotAction.ToggleSelect(screenshot.id)) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(screenshot.uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        if (screenshot.isSelected) {
                            Icon(
                                painter = painterResource(id = com.prography.ui.R.drawable.ic_check_box),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    DeleteConfirmDialog(
        isVisible = state.showDeleteDialog,
        selectedCount = state.selectedCount,
        onDismiss = { onAction(ScreenshotAction.DismissDeleteDialog) },
        onConfirm = { onAction(ScreenshotAction.ConfirmDelete) }
    )
}
