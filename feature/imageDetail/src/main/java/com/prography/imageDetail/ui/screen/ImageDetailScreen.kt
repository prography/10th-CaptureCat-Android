package com.prography.imageDetail.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.UiScreenshotModel
import com.prography.imageDetail.ui.content.ImageDetailContent
import com.prography.imageDetail.ui.contract.ImageDetailEffect
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.imageDetail.ui.viewmodel.ImageDetailViewModel
import com.prography.ui.theme.PrographyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ImageDetailScreen(
    screenshotIds: List<String>,
    currentIndex: Int = 0,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // 스크린샷 초기화
    LaunchedEffect(screenshotIds, currentIndex) {
        viewModel.initializeWithIds(screenshotIds, currentIndex)
    }

    // Handle effects
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                ImageDetailEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is ImageDetailEffect.ShowError -> {
                    // TODO: Show error message (could use SnackBar or Toast)
                    // For now, just log the error
                    println("ImageDetail Error: ${effect.message}")
                }

                ImageDetailEffect.ScreenshotDeleted -> {
                    // TODO: Show success message
                    println("Screenshot deleted successfully")
                }
            }
        }
    }

    ImageDetailContent(
        state = state,
        onAction = { action ->
            viewModel.sendAction(action)
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ImageDetailScreenPreview() {
    val sampleScreenshotIds = listOf(
        "1",
        "2",
        "3"
    )

    PrographyTheme {
        ImageDetailContent(
            state = ImageDetailState(
                screenshots = listOf(
                    UiScreenshotModel(
                        id = "1",
                        uri = "https://via.placeholder.com/300x400",
                        appName = "Instagram",
                        tags = listOf("쇼핑", "패션"),
                        isFavorite = false,
                        dateStr = "2024-01-15"
                    ),
                    UiScreenshotModel(
                        id = "2",
                        uri = "https://via.placeholder.com/300x400",
                        appName = "YouTube",
                        tags = listOf("여행", "음식"),
                        isFavorite = true,
                        dateStr = "2024-01-14"
                    ),
                    UiScreenshotModel(
                        id = "3",
                        uri = "https://via.placeholder.com/300x400",
                        appName = "Coupang",
                        tags = listOf("쇼핑", "생활용품"),
                        isFavorite = false,
                        dateStr = "2024-01-13"
                    )
                ),
                currentIndex = 0,
                currentScreenshot = UiScreenshotModel(
                    id = "1",
                    uri = "https://via.placeholder.com/300x400",
                    appName = "Instagram",
                    tags = listOf("쇼핑", "패션"),
                    isFavorite = false,
                    dateStr = "2024-01-15"
                ),
                availableTags = listOf("쇼핑", "패션", "여행", "음식", "생활용품"),
                isTagEditBottomSheetVisible = false,
                isDeleteDialogVisible = false,
                newTagText = "",
                isLoading = false
            ),
            onAction = {}
        )
    }
}