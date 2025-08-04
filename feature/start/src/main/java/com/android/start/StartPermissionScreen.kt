package com.android.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.R
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*

@Composable
fun StartPermissionScreen(
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(125.dp))

            // 상단 텍스트
            Text(
                text = "사진 접근 허용으로 스크린샷만 쏙!",
                style = headline01Bold,
                color = Text01,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "모든 사진에 대한 접근 허용을 해도 캡처캣은\n스크린샷만 불러와요",
                style = body01Regular.copy(lineHeight = 25.92.sp),
                color = Text02,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(100.dp))

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_permission),
                        contentDescription = null
                    )
                }
            }

        }

        // 하단 버튼
        UiPrimaryButton(
            text = "다음",
            onClick = onNext,
            state = ButtonState.Enabled,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 26.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StartPermissionScreenPreview() {
    PrographyTheme {
        StartPermissionScreen()
    }
}