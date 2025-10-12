package com.prography.organize.ui

import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeEffect
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.viewmodel.OrganizeViewModel
import com.prography.ui.component.TagAddBottomSheet
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun OrganizeScreen(
    entryPoint: String = "inbox",
    screenshots: List<OrganizeScreenshotItem>,
    currentIndex: Int = 0,
    onNavigateUp: () -> Unit,
    onComplete: () -> Unit,
    viewModel: OrganizeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val context = LocalContext.current

    var showAddTagBottomSheet by remember { mutableStateOf(false) }
    var currentScreenshotIdForTag by remember { mutableStateOf("") }

    // ⬇️ 시스템 삭제 인텐트 런처 (OK/취소 무관하게 완료 액션)
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { _ ->
        viewModel.sendAction(OrganizeAction.OnSystemDeleteFinished)
    }

    // 초기화
    LaunchedEffect(screenshots) {
        if (screenshots.isNotEmpty()) {
            viewModel.setEntryPoint(entryPoint)
            viewModel.initializeScreenshots(screenshots, currentIndex)
        }
    }

    // 효과 처리
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                OrganizeEffect.NavigateUp -> onNavigateUp()
                OrganizeEffect.NavigateToComplete -> onComplete()

                is OrganizeEffect.ShowAddTagBottomSheet -> {
                    currentScreenshotIdForTag = effect.screenshotId
                    showAddTagBottomSheet = true
                }

                // ✅ 시스템 삭제 알럿 요청
                is OrganizeEffect.RequestSystemDelete -> {
                    val uris = effect.uris
                    if (uris.isEmpty()) {
                        viewModel.sendAction(OrganizeAction.OnSystemDeleteFinished)
                        return@collect
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        try {
                            val request = MediaStore.createDeleteRequest(
                                context.contentResolver,
                                uris
                            )
                            deleteLauncher.launch(
                                IntentSenderRequest.Builder(request.intentSender).build()
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Timber.e(e, "Failed to send delete request")
                            viewModel.sendAction(OrganizeAction.OnSystemDeleteFinished)
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to launch delete request")
                            viewModel.sendAction(OrganizeAction.OnSystemDeleteFinished)
                        }
                    } else {
                        // API 30 미만: 시스템 알럿 없음. 가능한 범위 내에서 삭제 시도 후 완료로 진행
                        try {
                            uris.forEach { uri ->
                                runCatching { context.contentResolver.delete(uri, null, null) }
                                    .onFailure { Timber.w("Delete failed for $uri") }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Bulk delete fallback failed")
                        } finally {
                            viewModel.sendAction(OrganizeAction.OnSystemDeleteFinished)
                        }
                    }
                }
            }
        }
    }

    // Handle pager state for single mode
    val pagerState = rememberPagerState(
        initialPage = state.currentIndex
    ) { state.screenshots.size }

    val coroutineScope = rememberCoroutineScope()

    // Sync pager state with ViewModel state
    LaunchedEffect(state.currentIndex, state.organizeMode) {
        if (state.organizeMode == OrganizeMode.SINGLE &&
            pagerState.currentPage != state.currentIndex &&
            state.screenshots.isNotEmpty()
        ) {
            coroutineScope.launch {
                pagerState.scrollToPage(state.currentIndex)
            }
        }
    }

    // Update ViewModel when pager page changes
    LaunchedEffect(pagerState.currentPage) {
        if (state.organizeMode == OrganizeMode.SINGLE &&
            pagerState.currentPage != state.currentIndex
        ) {
            viewModel.sendAction(OrganizeAction.OnPageChange(pagerState.currentPage))
        }
    }

    // 완료 화면 표시 조건
    if (state.showCompletionMessage) {
        CompletionMessage(
            screenshotCount = state.screenshots.size,
            onNext = { viewModel.sendAction(OrganizeAction.OnCompletionNext) }
        )
    } else {
        OrganizeContent(
            state = state,
            pagerState = pagerState,
            onAction = viewModel::sendAction,
            getCurrentScreenshotTags = { viewModel.getCurrentScreenshotTags().map { it.name } } ,
            getCurrentScreenshotId = viewModel::getCurrentScreenshotId
        )
    }

    if (showAddTagBottomSheet) {
        TagAddBottomSheet(
            onAdd = { tagName ->
                viewModel.sendAction(
                    OrganizeAction.OnCreateNewTag(currentScreenshotIdForTag, tagName)
                )
                showAddTagBottomSheet = false
            },
            onDismiss = { showAddTagBottomSheet = false }
        )
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun OrganizeScreenFullPreview() {
    val mockScreenshots = listOf(
        OrganizeScreenshotItem(
            id = "1",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_1.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "2",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_2.png",
            isFavorite = true
        ),
        OrganizeScreenshotItem(
            id = "3",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_3.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "4",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_4.png",
            isFavorite = false
        ),
        OrganizeScreenshotItem(
            id = "5",
            uri = android.net.Uri.EMPTY,
            fileName = "screenshot_5.png",
            isFavorite = true
        )
    )

    OrganizeScreen(
        screenshots = mockScreenshots,
        currentIndex = 0,
        onNavigateUp = { },
        onComplete = { }
    )
}