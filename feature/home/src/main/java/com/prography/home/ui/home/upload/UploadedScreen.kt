package com.prography.home.ui.home.upload

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.ui.R
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.headline01Bold

@Composable
fun UploadedScreen(viewModel: UploadViewModel) {

    UploadedContent(
        onContinue = { viewModel.sendAction(UploadAction.Finish) }
    )
}

@Composable
private fun UploadedContent(
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 26.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 중앙 콘텐츠
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "모든 스크린샷 동기화 완료!",
                    style = headline01Bold,
                    color = Text01,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "이제 모든 기기에서 저장한\n스크린샷을 관리할 수 있어요.",
                    style = body01Regular,
                    color = Text03,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(R.drawable.img_uploaded),
                    contentDescription = null
                )
            }

            // 아래 버튼
            UiPrimaryButton (
                onClick = onContinue,
                text = "다음",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadedScreenPreview() {
    UploadedContent(
        onContinue = {}
    )
}