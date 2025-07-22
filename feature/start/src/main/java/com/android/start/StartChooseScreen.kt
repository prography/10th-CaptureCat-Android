package com.android.start

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prography.ui.R
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*

data class ScreenshotItem(
    val id: String,
    val uri: String,
)

@Composable
fun StartChooseScreen(
    viewModel: StartChooseViewModel = hiltViewModel(),
    maxSelectableImages: Int = 10,
    onFinishSelection: (List<ScreenshotItem>) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item(span = { GridItemSpan(3) }) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "시작하기 전에\n${state.screenshots.size}장의 스크린샷이 있어요",
                        style = headline02Bold,
                        color = Text01
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "나중에도 저장할 수 있으니 먼저 필요한 이미지만 골라보세요.",
                        style = body02Regular,
                        color = Text03
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(state.screenshots, key = { it.id }) { screenshot ->
                val isSelected = state.selectedScreenshots.contains(screenshot)
                Box(
                    modifier = Modifier
                        .aspectRatio(45f / 76f)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Primary else Gray04
                        )
                        .clickable {
                            viewModel.sendAction(
                                StartChooseAction.ToggleSelection(screenshot, maxSelectableImages)
                            )
                        }
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            UiPrimaryButton(
                text = "정리하기 (${state.selectedScreenshots.size}/$maxSelectableImages)",
                state = if (state.selectedScreenshots.isNotEmpty()) ButtonState.Enabled else ButtonState.Disabled,
                onClick = { onFinishSelection(state.selectedScreenshots) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
