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
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.ButtonState
import com.prography.ui.component.DeleteConfirmDialog
import com.prography.ui.component.UiBasicDialog
import com.prography.ui.component.UiButtonText
import com.prography.ui.component.UiCheckBox
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.OverlayDim
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.headline02Regular
import com.prography.ui.theme.subhead02Bold
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

    // 업로드 모드 선택 제한
    val uploadMax = 20

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) {
        pagingItems.refresh()
    }
    LaunchedEffect(state.refreshVersion) { pagingItems.refresh() }

    // 전체 스크린샷 개수
    var totalScreenshotCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { totalScreenshotCount = getTotalScreenshotCount(context) }

    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onAction(ScreenshotAction.ConfirmDelete)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ========= HEADER =========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 8.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                // Organize 모드에서만 우측 액션 노출(삭제)
                if (mode == StorageMode.Organize) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        style = body02Regular,
                        color = Text03,
                        modifier = Modifier.clickable { onAction(ScreenshotAction.DeleteSelected) }
                    )
                }
            }
        }

        // ========= CONTENT =========
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (!state.isLoggedIn) Modifier.blur(12.dp) else Modifier),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Organize 전용 상단 행: 전체 선택
                if (mode == StorageMode.Organize) {
                    item(span = { GridItemSpan(3) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(vertical = 10.dp, horizontal = 16.dp),
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
                                        val allIds = (0 until pagingItems.itemCount)
                                            .mapNotNull { idx -> pagingItems[idx]?.id }
                                        ScreenshotAction.SelectAll(allIds)
                                    }
                                    onAction(action)
                                }
                            )
                        }
                    }
                }

                // 그리드 아이템들
                items(count = pagingItems.itemCount) { index ->
                    val screenshot = pagingItems[index] ?: return@items
                    val isSelected = state.selectedItems.contains(screenshot.id)
                    val isOrganized = state.organizedScreenshotIds.contains(screenshot.id)

                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = when {
                                    isSelected -> Primary
                                    else -> Gray04
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxWidth()
                            .aspectRatio(45f / 76f)
                            .clickable {
                                // 업로드 모드: 20장 제한
                                if (mode == StorageMode.Upload &&
                                    !isSelected &&
                                    state.selectedCount >= uploadMax
                                ) {
                                    // TODO: 필요하면 토스트/스낵바 액션으로 안내
                                    return@clickable
                                }
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

                        // Organize 전용: 정리 완료 배지
                        if (mode == StorageMode.Organize && isOrganized && !isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = Color.White, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // append 로딩
                if (pagingItems.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(3) }) {
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

                // append 에러
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

            // 로그인 유도
            if (!state.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickableWithoutRipple(enabled = true, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    UiLabelAddButton(
                        text = stringResource(R.string.storage_login_required),
                        size = ButtonSize.LARGE,
                        onClick = { onAction(ScreenshotAction.NavigateToLogin) },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // ========= FOOTER: 업로드 모드 전용 CTA =========
            if (mode == StorageMode.Upload && state.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFFF5F6F8))
                        .padding(16.dp)
                ) {
                    val enabled = state.selectedCount in 1..uploadMax
                    UiPrimaryButton(
                        text = stringResource(R.string.storage_selection_count, state.selectedCount),
                        onClick = { onAction(ScreenshotAction.OrganizeSelected) },
                        state = if (enabled) ButtonState.Enabled else ButtonState.Disabled,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 26.dp)
                    )
                }
            }
        }
    }

    // ========= DIALOGS: 정리 모드 전용 =========
    if (mode == StorageMode.Organize) {
        DeleteConfirmDialog(
            isVisible = state.showDeleteDialog && state.selectedCount > 0,
            selectedCount = state.selectedCount,
            onDismiss = { onAction(ScreenshotAction.DismissDeleteDialog) },
            onConfirm = {
                val selectedIds = state.selectedItems.toList()
                val selectedItems = (0 until pagingItems.itemCount).mapNotNull { index ->
                    pagingItems[index]?.takeIf { it.id in selectedIds }
                }
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

        UiBasicDialog(
            isVisible = state.showDeleteDialog && state.selectedCount == 0,
            info = stringResource(R.string.storage_delete_select_message),
            confirmButtonText = stringResource(R.string.common_confirm),
            onConfirm = { onAction(ScreenshotAction.DismissDeleteDialog) }
        )
    }
}
// 전체 스크린샷 개수만 가져오는 함수
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ScreenshotOrganizeContentPreview() {
    val fakeScreenshots = List(9) { index ->
        com.prography.home.ui.storage.contract.ScreenshotItem(
            id = "id_$index",
            uri = android.net.Uri.parse("file:///fake_path_to_file_$index.jpg"),
            dateGroup = "",
            isSelected = index % 2 == 0,
            fileName = "screenshot_$index.jpg",
            isOrganized = index % 3 == 0 // Add dummy isOrganized field
        )
    }

    val fakeState = ScreenshotState(
        groupedScreenshots = mapOf("" to fakeScreenshots), // Use flat list
        totalCount = fakeScreenshots.size,
        selectedCount = fakeScreenshots.count { it.isSelected },
        isSelectionMode = true,
        showDeleteDialog = false,
        isLoggedIn = false // You need to set isLoggedIn to false here for the preview
    )

    ScreenshotStorageScreen(
        state = fakeState,
        onAction = {},
        mode = StorageMode.Organize
    )
}
