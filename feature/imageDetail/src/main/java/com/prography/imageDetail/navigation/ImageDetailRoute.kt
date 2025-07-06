package com.prography.imageDetail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.imageDetail.ui.screen.ImageDetailScreen
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageDetailRouteViewModel @Inject constructor(
    private val getAllScreenshotsUseCase: GetAllScreenshotsUseCase
) : ViewModel() {

    private val _screenshots = MutableStateFlow<List<UiScreenshotModel>>(emptyList())
    val screenshots: StateFlow<List<UiScreenshotModel>> = _screenshots.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadScreenshots(screenshotIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allScreenshots = getAllScreenshotsUseCase().firstOrNull() ?: emptyList()

                // screenshotIds 순서대로 정렬된 스크린샷 목록 생성
                val orderedScreenshots = screenshotIds.mapNotNull { id ->
                    allScreenshots.find { it.id == id }
                }

                _screenshots.value = orderedScreenshots
            } catch (e: Exception) {
                _screenshots.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun ImageDetailRoute(
    navigationHelper: NavigationHelper,
    screenshotIds: List<String>,
    currentIndex: Int
) {
    val viewModel: ImageDetailRouteViewModel = hiltViewModel()
    val screenshots by viewModel.screenshots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(screenshotIds) {
        viewModel.loadScreenshots(screenshotIds)
    }

    when {
        isLoading -> {

        }
        screenshots.isEmpty() -> {
            // No screenshots found, navigate back
            LaunchedEffect(Unit) {
                navigationHelper.navigate(NavigationEvent.Up)
            }
        }

        else -> {
            ImageDetailScreen(
                screenshots = screenshots,
                currentIndex = currentIndex,
                onNavigateBack = {
                    navigationHelper.navigate(NavigationEvent.Up)
                }
            )
        }
    }
}