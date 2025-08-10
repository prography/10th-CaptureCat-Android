package com.prography.home.ui.home.upload

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.home.contract.HomeAction
import com.prography.ui.R
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PrimaryLow
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.headline01Bold
import com.prography.ui.theme.subhead02Bold

@Composable
fun UploadingScreen(viewModel: UploadViewModel) {
    val state by viewModel.uiState.collectAsState()

    BackHandler(enabled = true) {
        viewModel.sendAction(UploadAction.CancelUpload)
    }

    UploadingContent(
        state = state,
        onAction = { action -> viewModel.sendAction(action) }
    )

    UiCommonDialog(
        title = "여기서 그만둘까요?",
        message = "지금 나가면 스크린샷을 다시 불러올 수 없어요.",
        leftButtonText = "계속",
        rightButtonText = "그만두기",
        onDismiss = { viewModel.sendAction(UploadAction.DismissCancelDialog) },
        onConfirm = { viewModel.sendAction(UploadAction.ConfirmCancelUpload) },
        isVisible = state.showConfirmDialog
    )
}


@Composable
fun UploadingContent(
    state: UploadState,
    onAction: (UploadAction) -> Unit
) {
    val percent = if (state.totalCount == 0) 0 else (state.uploadedCount * 100) / state.totalCount

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 10.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_backward), // ← 아이콘 교체
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(32.dp)
                    .clickableWithoutRipple { onAction(UploadAction.CancelUpload) }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.img_uploading),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "모든 스크린샷을\n동기화 하고 있어요",
                    style = headline01Bold,
                    color = Text01,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tip. 즐겨찾기로 하면 홈에서\n더 자주 볼 수 있어요.",
                    style = body01Regular,
                    color = Text03,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = PrimaryLow,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "$percent% 완료",
                        color = Primary,
                        style = subhead02Bold
                    )
                }
            }
        }
    }
}