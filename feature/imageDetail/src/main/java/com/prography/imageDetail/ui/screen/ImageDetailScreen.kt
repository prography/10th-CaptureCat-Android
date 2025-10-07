package com.prography.imageDetail.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.TagModel
import com.prography.domain.model.UiScreenshotModel
import com.prography.imageDetail.ui.content.ImageDetailContent
import com.prography.imageDetail.ui.contract.ImageDetailEffect
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.imageDetail.ui.viewmodel.ImageDetailViewModel
import com.prography.ui.theme.PrographyTheme
import com.prography.util.MixpanelUtil
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ImageDetailScreen(
    screenshotIds: List<String>,
    currentIndex: Int = 0,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageDetailViewModel = hiltViewModel(),
    entryPoint: String = "Unknown"
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // 스크린샷 초기화
    LaunchedEffect(screenshotIds, currentIndex) {
        MixpanelUtil.track(
            "view_image_detail",
            mapOf(
                "image_id" to (state.currentScreenshot?.id ?: 0),
                "entry_point" to entryPoint
            )
        )
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
