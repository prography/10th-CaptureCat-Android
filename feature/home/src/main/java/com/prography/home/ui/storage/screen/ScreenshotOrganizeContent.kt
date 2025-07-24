package com.prography.home.ui.storage.screen

import android.app.Activity
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.blur
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.home.ui.storage.contract.ScreenshotState
import com.prography.home.ui.storage.viewmodel.ScreenshotViewModel
import com.prography.ui.R
import com.prography.ui.component.DeleteConfirmDialog
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.UiCheckBox
import com.prography.ui.component.UiButtonText
import com.prography.ui.component.UiBasicDialog
import com.prography.ui.component.ButtonSize
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Gray04
import coil3.compose.rememberAsyncImagePainter
import com.prography.home.ui.storage.permission.DeleteHelper
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.OverlayDim
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.subhead02Bold
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.body02Regular
import timber.log.Timber

@Composable
fun ScreenshotOrganizeContent(
    state: ScreenshotState,
    onAction: (ScreenshotAction) -> Unit,
    viewModel: ScreenshotViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pagingItems = viewModel.screenshotsPagingFlow.collectAsLazyPagingItems()

    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onAction(ScreenshotAction.ConfirmDelete)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 타이틀 부분 (블러 처리 안됨)
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
                Column {
                    Text(
                        text = "임시보관함",
                        style = headline02Bold,
                        color = Text01
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${pagingItems.itemCount}개의 스크린샷이 있어요",
                        style = body02Regular,
                        color = Text01
                    )
                }
                UiButtonText(
                    text = "다음",
                    onClick = { onAction(ScreenshotAction.OrganizeSelected) },
                    enabled = state.selectedCount > 0
                )
            }
        }

        // 메인 컨텐츠
        Box(
            modifier = Modifier.weight(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (!state.isLoggedIn) Modifier.blur(12.dp) else Modifier),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 전체 선택/삭제 헤더
                item(span = { GridItemSpan(3) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UiCheckBox(
                            text = "전체 선택",
                            isChecked = state.isAllSelected,
                            onCheckedChange = {
                                val action = if (state.isAllSelected)
                                    ScreenshotAction.CancelSelection
                                else
                                    ScreenshotAction.SelectAll
                                onAction(action)
                            }
                        )
                        Text(
                            text = "선택 삭제",
                            style = body02Regular,
                            color = Text03,
                            modifier = Modifier.clickable {
                                onAction(ScreenshotAction.DeleteSelected)
                            }
                        )
                    }
                }

                // Paging된 스크린샷들
                items(count = pagingItems.itemCount) { index ->
                    pagingItems[index]?.let { screenshot ->
                        val isSelected = state.selectedItems.contains(screenshot.id)

                        Box(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) Primary else Gray04
                                )
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable {
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
                        }
                    }
                }

                // 로딩 인디케이터
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

                // 에러 상태
                if (pagingItems.loadState.append is LoadState.Error) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "더 불러오기 실패",
                                color = Color.Red,
                                modifier = Modifier.clickable {
                                    pagingItems.retry()
                                }
                            )
                        }
                    }
                }
            }

            // 로그인 유도 버튼
            if (!state.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickableWithoutRipple(enabled = true, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    UiLabelAddButton(
                        text = "로그인 후 이용하기",
                        size = ButtonSize.LARGE,
                        onClick = { onAction(ScreenshotAction.NavigateToLogin) },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // 선택 개수 표시 (로그인된 경우에만)
            if (state.isLoggedIn) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .background(color = OverlayDim, shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.selectedCount}/20",
                        color = PureWhite,
                        style = subhead02Bold
                    )
                }
            }
        }
    }

    // 삭제 확인 다이얼로그
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
                }
            )
        }
    )

    UiBasicDialog(
        isVisible = state.showDeleteDialog && state.selectedCount == 0,
        info = "삭제할 이미지를 선택해주세요.",
        confirmButtonText = "확인",
        onConfirm = { onAction(ScreenshotAction.DismissDeleteDialog) }
    )
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
            fileName = "screenshot_$index.jpg"
        )
    }

    val fakeState = com.prography.home.ui.storage.contract.ScreenshotState(
        groupedScreenshots = mapOf("" to fakeScreenshots), // Use flat list
        totalCount = fakeScreenshots.size,
        selectedCount = fakeScreenshots.count { it.isSelected },
        isSelectionMode = true,
        showDeleteDialog = false,
        isLoggedIn = false // You need to set isLoggedIn to false here for the preview
    )

    ScreenshotOrganizeContent(
        state = fakeState,
        onAction = {}
    )
}
