package com.prography.home.ui.home.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UploadRoute() {
    val viewModel: UploadViewModel = hiltViewModel()
    val effectFlow = viewModel.effect

    var currentScreen by remember { mutableStateOf(ScreenState.UPLOADING_SCREEN) }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is UploadEffect.NavigateToUploaded -> currentScreen = ScreenState.UPLOADED_SCREEN
            }
        }
    }

    when (currentScreen) {
        ScreenState.UPLOADING_SCREEN -> {
            UploadingScreen(viewModel = viewModel)
        }
        ScreenState.UPLOADED_SCREEN -> {
            UploadedScreen(viewModel = viewModel)
        }
    }
}

enum class ScreenState {
    UPLOADING_SCREEN,
    UPLOADED_SCREEN
}