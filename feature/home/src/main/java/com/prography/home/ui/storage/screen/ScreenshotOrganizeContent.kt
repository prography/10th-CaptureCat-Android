package com.prography.home.ui.storage.screen

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import coil3.compose.rememberAsyncImagePainter
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.contract.ScreenshotState
import com.prography.home.ui.storage.permission.DeleteHelper
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.navigation.StorageMode
import com.prography.ui.R
import com.prography.ui.component.*
import com.prography.ui.theme.*
import timber.log.Timber

@Composable
fun ScreenshotStorageScreen(
    mode: StorageMode,
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit,
    viewModel: ScreenshotViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()
    val uploadMax = 20

    // refresh
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) { pagingItems.refresh() }
    LaunchedEffect(state.refreshVersion) { pagingItems.refresh() }

    // total count
    var totalScreenshotCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { totalScreenshotCount = getTotalScreenshotCount(context) }

    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onAction(ScreenshotAction.ConfirmDelete)
        }
    }

    // ✅ Scaffold로 전체 레이아웃 구성
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ScreenshotHeader(
                mode = mode,
                totalScreenshotCount = totalScreenshotCount,
                state = state,
                onAction = onAction
            )
        },
        bottomBar = {
            if (mode == StorageMode.Upload && state.isLoggedIn) {
                ScreenshotFooter(
                    state = state,
                    uploadMax = uploadMax,
                    onAction = onAction
                )
            }
        }
    ) { innerPadding ->

        when {
            pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    UiEmptyState(
                        title = "갤러리에 캡처 이미지가 없어요.",
                        info  = "화면 캡처 후 이용해주세요."
                    )
                }
            }
            else -> {
                // ✅ 스크롤 가능한 본문: 사진 목록
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .then(if (!state.isLoggedIn) Modifier.blur(12.dp) else Modifier),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // 아이템
                    items(count = pagingItems.itemCount) { index ->
                        val screenshot = pagingItems[index] ?: return@items
                        val isSelected = state.selectedItems.contains(screenshot.id)
                        val isOrganized = state.organizedScreenshotIds.contains(screenshot.id)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) Color(0xCCFF6600) else Divider,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .fillMaxWidth()
                                .aspectRatio(45f / 76f)
                                .clickable {
                                    if (mode == StorageMode.Upload &&
                                        !isSelected &&
                                        state.selectedCount >= uploadMax
                                    ) return@clickable
                                    onAction(ScreenshotAction.ToggleSelect(screenshot.id))
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(screenshot.uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Icon(
                                painter = painterResource(
                                    id = if (isSelected)
                                        R.drawable.ic_check_box_able
                                    else
                                        R.drawable.ic_check_box_unchecked
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp),
                                tint = Color.Unspecified
                            )

                            // Organize 모드일 때 상단 주황 띠
                            if (mode == StorageMode.Organize && isOrganized && !isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .align(Alignment.TopCenter)
                                        .background(Primary)
                                )
                            }
                        }
                    }

                    // append loading
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Primary)
                            }
                        }
                    }

                    // append error
                    if (pagingItems.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(3) }) {
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
                }
            }
        }
    }

    // ========= DIALOGS =========
    if (mode == StorageMode.Organize) {
        DeleteConfirmDialog(
            isVisible = state.showDeleteDialog && state.selectedCount > 0,
            selectedCount = state.selectedCount,
            onDismiss = { onAction(ScreenshotAction.DismissDeleteDialog) },
            onConfirm = {
                val selectedIds = state.selectedItems.toList()
                val selectedItems = (0 until pagingItems.itemCount)
                    .mapNotNull { index -> pagingItems[index]?.takeIf { it.id in selectedIds } }
                DeleteHelper.deleteScreenshots(
                    context = context,
                    screenshots = selectedItems,
                    deleteLauncher = deleteLauncher,
                    onDeleteCompleted = {
                        onAction(ScreenshotAction.ConfirmDelete)
                        onAction(ScreenshotAction.RefreshScreenshots)
                    }
                )
            }
        )
    }
}

@Composable
private fun ScreenshotHeader(
    mode: StorageMode,
    totalScreenshotCount: Int,
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_backward),
                    contentDescription = stringResource(id = com.prography.ui.R.string.common_back),
                    tint = Text02,
                    modifier = Modifier.clickableWithoutRipple { onAction(ScreenshotAction.Back) }
                )
                Text(
                    text = when (mode) {
                        StorageMode.Upload -> stringResource(R.string.storage_upload_title)
                        StorageMode.Organize -> stringResource(R.string.storage_delete_title)
                    },
                    style = headline02Bold,
                    color = Text01
                )
                Spacer(modifier = Modifier.width(4.dp))

                val count = when (mode) {
                    StorageMode.Upload ->
                        if (state.selectedCount > 0) state.selectedCount else 0
                    StorageMode.Organize ->
                        if (state.selectedCount > 0) state.selectedCount else totalScreenshotCount
                }

                Text(
                    text = if (count > 0) count.toString() else "",
                    style = headline02Regular,
                    color = Text03
                )
            }

            if (mode == StorageMode.Organize && state.totalCount > 0) {
                Text(
                    text = stringResource(R.string.common_delete),
                    style = body02Regular,
                    color = Text03,
                    modifier = Modifier.clickable { onAction(ScreenshotAction.DeleteSelected) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Divider, thickness = 1.dp)

        if (mode == StorageMode.Organize && state.totalCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UiCheckBox(
                    text = stringResource(R.string.common_all_select),
                    isChecked = state.isAllSelected,
                    onCheckedChange = {
                        val action = if (state.isAllSelected) {
                            ScreenshotAction.CancelSelection
                        } else {
                            ScreenshotAction.SelectAll
                        }
                        onAction(action)
                    }
                )
            }
        }
    }
}

@Composable
private fun ScreenshotFooter(
    state: ScreenshotState,
    uploadMax: Int,
    onAction: (ScreenshotAction) -> Unit
) {
    Column{
        HorizontalDivider(color = Divider, thickness = 1.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            val enabled = state.selectedCount in 1..uploadMax
            UiLabelAddButton(
                text = stringResource(R.string.storage_selection_count, state.selectedCount),
                onClick = { onAction(ScreenshotAction.OrganizeSelected) },
                state = if (enabled) ButtonState.Enabled else ButtonState.Disabled,
                size = ButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

private fun getTotalScreenshotCount(context: Context): Int {
    return try {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Screenshots")
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        val count = cursor?.count ?: 0
        cursor?.close()
        count
    } catch (e: Exception) {
        Timber.e(e, "Error getting total screenshot count")
        0
    }
}
