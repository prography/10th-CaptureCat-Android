package com.prography.organize.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.headline01Bold


@Composable
fun CompletionMessage(
    screenshotCount: Int,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${screenshotCount}장 정리 완료!",
            style = headline01Bold,
            color = Text01,
            modifier = Modifier.padding(top = 150.dp, bottom = 8.dp)
        )

        Text(
            text = "즐겨찾기한 스크린샷은\n홈에서 더 자주 만날 수 있어요.",
            style = body01Regular.copy(lineHeight = 25.92.sp),
            color = Text02,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Image(
            painter = painterResource(id = com.prography.ui.R.drawable.ic_organize_complete),
            contentDescription = "Completion Icon",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 여백 추가해서 아래로 밀기
        Spacer(modifier = Modifier.weight(1f))

        // 하단 고정 광고 배너 & 버튼
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "광고 배너",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            UiPrimaryButton(
                onClick = onNext,
                text = "다음",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompletionMessagePreview() {
    CompletionMessage(
        screenshotCount = 5,
        onNext = {}
    )
}