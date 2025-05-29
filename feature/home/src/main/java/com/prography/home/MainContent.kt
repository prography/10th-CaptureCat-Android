package com.prography.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun MainContent(
    viewModel: ToDoViewModel = hiltViewModel(),
    onNavigateToScreenshotGallery: () -> Unit
) {


    val state by viewModel.photos.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // 메인 화면 콘텐츠

        // 스크린샷 갤러리 이동 버튼
        Button(
            onClick = onNavigateToScreenshotGallery,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("스크린샷 갤러리")
        }
    }
}


@Composable
fun YourAppTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
