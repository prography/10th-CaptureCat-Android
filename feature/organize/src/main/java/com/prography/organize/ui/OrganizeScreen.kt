package com.prography.organize.ui

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.components.*
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeEffect
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.viewmodel.OrganizeViewModel
import kotlinx.coroutines.launch

@Composable
fun OrganizeScreen(
    screenshots: List<OrganizeScreenshotItem>,
    currentIndex: Int = 0,
    onNavigateUp: () -> Unit,
    onComplete: () -> Unit,
    viewModel: OrganizeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    var showAddTagBottomSheet by remember { mutableStateOf(false) }
    var currentScreenshotIdForTag by remember { mutableStateOf("") }

    // Initialize screenshots when the screen is first composed
    LaunchedEffect(screenshots) {
        if (screenshots.isNotEmpty()) {
            viewModel.initializeScreenshots(screenshots, currentIndex)
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                OrganizeEffect.NavigateUp -> onNavigateUp()
                OrganizeEffect.NavigateToComplete -> onComplete()
                is OrganizeEffect.ShowAddTagBottomSheet -> {
                    currentScreenshotIdForTag = effect.screenshotId
                    showAddTagBottomSheet = true
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