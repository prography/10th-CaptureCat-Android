package com.android.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.prography.ui.R
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*

data class ScreenshotItem(
    val id: String,
    val uri: String, // URI로 이미지 확인 가능
)

@Composable
fun StartChooseScreen(
    viewModel: StartChooseViewModel = hiltViewModel(), // Hilt로 ViewModel 주입
    maxSelectableImages: Int = 10,
    onFinishSelection: (List<ScreenshotItem>) -> Unit
) {
    // 스크린샷 로드 (최초 로드 시 데이터 불러옴)
    LaunchedEffect(Unit) {
        viewModel.loadScreenshots()
    }

    val screenshots = viewModel.screenshots
    val selectedImages = viewModel.selectedScreenshots

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp) // 버튼 영역 확보
        ) {
            // 🔹 헤더 영역
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "시작하기 전에\n${screenshots.size}장의 스크린샷이 있어요",
                        style = headline02Bold,
                        color = Text01
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "나중에도 저장할 수 있지만, 먼저 필요한 이미지만 클릭하세요.",
                        style = body02Regular,
                        color = Text03
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 🔹 Grid 영역
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 9999.dp), // 💡 lazyColumn 안에선 무한 확장 막기
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    userScrollEnabled = false // 스크롤은 LazyColumn이 담당
                ) {
                    items(screenshots) { screenshot ->
                        val isSelected = screenshot in selectedImages
                        Box(
                            modifier = Modifier
                                .aspectRatio(45f / 76f)
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) Primary else Gray04
                                )
                                .clickable { viewModel.toggleSelection(screenshot, maxSelectableImages) }
                        ) {
                            AsyncImage(
                                model = screenshot.uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Icon(
                                painter = painterResource(
                                    id = if (isSelected) R.drawable.ic_check_box_able
                                    else R.drawable.ic_check_box_unchecked
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
            }
        }

        // ✅ 하단 고정 버튼
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            UiPrimaryButton(
                text = "정리하기 (${selectedImages.size}/$maxSelectableImages)",
                state = if (selectedImages.isNotEmpty()) ButtonState.Enabled else ButtonState.Disabled,
                onClick = { onFinishSelection(selectedImages) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}