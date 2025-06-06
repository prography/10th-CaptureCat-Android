package com.prography.onboarding.screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.onboarding.R
import com.prography.onboarding.screen.contract.OnboardingAction
import com.prography.onboarding.screen.contract.OnboardingState
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.UiUnderlinedTextButton

@Composable
fun OnboardingContent(
    state: OnboardingState, onAction: (OnboardingAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
                    .size(40.dp),
                painter = painterResource(id = com.prography.ui.R.drawable.ic_close),
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "시작하기 전에",
                style = TextStyle(
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF000000),
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "로그인을 하면 스크린샷 기록을\n모든 디바이스에서 관리할 수 있어요.",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    textAlign = TextAlign.Center,
                )
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            UiPrimaryButton(
                text = "로그인",
                onClick = { onAction(OnboardingAction.LoginClicked) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            UiUnderlinedTextButton(
                fullText = "나중에 할게요",
                underlineTarget = "나중에 할게요",
                fontSize = 14.sp,
                onClick = { onAction(OnboardingAction.SkipClicked) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginContentPreview() {
    OnboardingContent(
        state = OnboardingState(), // 기본 상태 or 샘플 데이터
        onAction = {} // 빈 람다 전달
    )
}
