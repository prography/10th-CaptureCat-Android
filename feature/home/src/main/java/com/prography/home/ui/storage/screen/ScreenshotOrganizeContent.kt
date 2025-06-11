package com.prography.home.ui.storage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.contract.ScreenshotState

@Composable
fun ScreenshotOrganizeContent(
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ScreenshotTopBar(
            isSelectionMode = state.isSelectionMode,
            selectedCount = state.selectedCount,
            totalCount = state.groupedScreenshots.size,
            isAllSelected = state.isAllSelected,
            onAction = onAction
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            state.groupedScreenshots.forEach { (date, items) ->
                item {
                    Text(
                        text = date,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
                items(items.chunked(3)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { screenshot ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable { onAction(ScreenshotAction.ToggleSelect(screenshot.id)) }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(screenshot.uri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
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
        }

        if (state.isSelectionMode) {
            ScreenshotBottomBar(
                selectedCount = state.selectedCount,
                onAction = onAction
            )
        }
    }
}
