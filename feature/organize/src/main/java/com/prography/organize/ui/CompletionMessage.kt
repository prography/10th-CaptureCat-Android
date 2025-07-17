package com.prography.organize.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prography.ui.component.UiButtonText
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.body02Regular

@Composable
fun CompletionMessage(
    screenshotCount: Int,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 완료 메시지
            Text(
                text = "${screenshotCount}장 태그 완료!",
                style = headline02Bold,
                color = Text01,
                textAlign = TextAlign.Center
            )

            Text(
                text = "스크린샷을 성공적으로 정리했어요",
                style = body02Regular,
                color = Text02,
                textAlign = TextAlign.Center
            )
        }

        // 다음 버튼
        UiButtonText(
            text = "다음",
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}