package com.prography.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotGalleryScreen(
    onNavigateUp: () -> Unit
) {
    // 선택된 스크린샷 목록 상태
    val selectedScreenshots = remember { mutableStateListOf<Uri>() }

    // 스크린샷 선택 결과 처리기
    val screenshotLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // 결과 데이터에서 URI 가져오기
            result.data?.let { data ->
                when {
                    data.clipData != null -> {
                        // 다중 선택 처리
                        val clipData = data.clipData!!
                        for (i in 0 until clipData.itemCount) {
                            clipData.getItemAt(i).uri?.let { uri ->
                                selectedScreenshots.add(uri)
                            }
                        }
                    }

                    data.data != null -> {
                        // 단일 선택 처리
                        data.data?.let { uri ->
                            selectedScreenshots.add(uri)
                        }
                    }

                    else -> {
                        val multipleUris =
                            data.getParcelableArrayListExtra<Uri>("selected_screenshots")
                        multipleUris?.let { uris ->
                            if (uris.isNotEmpty()) {
                                selectedScreenshots.addAll(uris)
                            }
                        }
                    }
                }
            }
        }
    }

    val context = LocalContext.current

    val launchScreenshotPicker = {
        val intent = Intent(context, ScreenshotPickerActivity::class.java)
        intent.putExtra("multi_select_mode", true)
        screenshotLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스크린샷 갤러리") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        // 뒤로 가기 아이콘
                        Text("←")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = launchScreenshotPicker
            ) {
                // 추가 아이콘
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedScreenshots.isEmpty()) {
                // 스크린샷이 없을 때
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("선택된 스크린샷이 없습니다")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = launchScreenshotPicker
                    ) {
                        Text("스크린샷 추가")
                    }
                }
            } else {
                // 선택된 스크린샷 표시
                ScreenshotGrid(screenshots = selectedScreenshots) { uri ->
                    selectedScreenshots.remove(uri)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ScreenshotGrid(
    screenshots: List<Uri>,
    onLongPress: (Uri) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(screenshots) { uri ->
            // 각 스크린샷 아이템
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(0.75f)
                    .clickable { /* 클릭 시 동작 (필요시 추가) */ }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongPress(uri) }
                        )
                    }
            ) {
                GlideImage(
                    model = uri,
                    contentDescription = "Screenshot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}