package com.prography.home.ui.storage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.contract.ScreenshotItem
import com.prography.home.ui.storage.contract.ScreenshotState
import com.prography.ui.R
import com.prography.ui.component.DeleteConfirmDialog
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Gray04
import coil3.compose.rememberAsyncImagePainter
import com.prography.ui.theme.OverlayDim
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.subhead02Bold
import timber.log.Timber

@Composable
fun ScreenshotOrganizeContent(
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit
) {
    val context = LocalContext.current // Get context for ContentResolver

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ScreenshotTopBar(
                isSelectionMode = state.isSelectionMode,
                selectedCount = state.selectedCount,
                totalCount = state.groupedScreenshots.values.flatten().size,
                isAllSelected = state.isAllSelected,
                onAction = { action ->
                    when (action) {
                        ScreenshotAction.SelectAll -> {
                            val updatedScreenshots = state.groupedScreenshots.values.flatten()
                                .map { it.copy(isSelected = true) }

                            val updatedState = ScreenshotState(
                                groupedScreenshots = mapOf("" to updatedScreenshots),
                                totalCount = updatedScreenshots.size,
                                selectedCount = updatedScreenshots.size,
                                isAllSelected = true
                            )
                        }

                        ScreenshotAction.DeleteSelected -> {
                            val selectedItemCount = state.selectedCount
                            if (selectedItemCount > 0) {
                                onAction(ScreenshotAction.ShowDeleteDialog)
                            }
                        }

                        else -> onAction(action)
                    }
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(state.groupedScreenshots.values.flatten()) { screenshot -> // Flatten grouped screenshots into a list
                    Box(
                        modifier = Modifier
                            .border(
                                width = if (screenshot.isSelected) 3.dp else 2.dp,
                                color = if (screenshot.isSelected) Primary else Gray04
                            )
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
                                painter = painterResource(id = R.drawable.ic_check_box_able),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp),
                                tint = Color.Unspecified
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check_box_unchecked),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp) // ← 하단에서 8.dp 띄우기
                .background(color = OverlayDim, shape = RoundedCornerShape(50.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${state.selectedCount}/20",
                color = PureWhite,
                style = subhead02Bold
            )
        }
    }


    DeleteConfirmDialog(
        isVisible = state.showDeleteDialog,
        selectedCount = state.selectedCount,
        onDismiss = { onAction(ScreenshotAction.DismissDeleteDialog) },
        onConfirm = {
            onAction(ScreenshotAction.ConfirmDelete)
            // Implement deletion logic using ContentResolver
            val selectedItems = state.groupedScreenshots.values.flatten()
                .filter { it.isSelected }
            selectedItems.forEach { screenshot ->
                try {
                    context.contentResolver.delete(screenshot.uri, null, null)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to delete file: ${screenshot.uri}")
                }
            }
            // Refresh screenshots
            onAction(ScreenshotAction.RefreshScreenshots)
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ScreenshotOrganizeContentPreview() {
    val fakeScreenshots = List(9) { index ->
        ScreenshotItem(
            id = "id_$index",
            uri = android.net.Uri.parse("file:///fake_path_to_file_$index.jpg"),
            dateGroup = "",
            isSelected = index % 2 == 0,
            fileName = "screenshot_$index.jpg"
        )
    }

    val fakeState = ScreenshotState(
        groupedScreenshots = mapOf("" to fakeScreenshots), // Use flat list
        totalCount = fakeScreenshots.size,
        selectedCount = fakeScreenshots.count { it.isSelected },
        isSelectionMode = true,
        showDeleteDialog = false
    )

    ScreenshotOrganizeContent(
        state = fakeState,
        onAction = {}
    )
}
