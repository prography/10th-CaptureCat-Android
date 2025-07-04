package com.prography.auth.route.screen.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.auth.route.screen.contract.LoginAction
import com.prography.auth.route.screen.contract.LoginState
import com.prography.ui.R
import com.prography.ui.component.UnderlinedClickableText
import com.prography.ui.theme.subhead01Bold

@Composable
fun LoginContent(state: LoginState, onAction: (LoginAction) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // 상단 오른쪽 "나중에 하기"
        UnderlinedClickableText(
            text = "나중에 하기",
            onClick = { onAction(LoginAction.ClickSkip) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 33.dp, end = 16.dp)
        )

        // 전체 세로 레이아웃
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // 상단 여백 (상태바 포함)

            // 중앙 이미지 묶음 (위~버튼 시작의 정확한 중앙)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_login_cat),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(R.drawable.ic_login_logo),
                    contentDescription = null
                )
            }

            // 하단 버튼 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KakaoLoginButton { onAction(LoginAction.ClickKakao) }
                GoogleLoginButton { onAction(LoginAction.ClickGoogle) }
                Spacer(modifier = Modifier.height(12.dp))
                AgreementText()
            }
        }
    }
}

@Composable
fun KakaoLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500)),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kakao_login),
                contentDescription = "카카오 로그인 아이콘",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.login_kakao),
                color = Color.Black,
                style = subhead01Bold
            )
        }
    }
}

@Composable
fun GoogleLoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(1.dp, Color(0xFF747775), shape = RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_login),
                contentDescription = "Google 로그인 아이콘",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.login_google),
                color = Color.Black,
                style = subhead01Bold
            )
        }
    }
}

@Composable
fun AgreementText() {
    val context = LocalContext.current

    val annotatedText = buildAnnotatedString {
        append("가입하면 캡처캣의\n")

        pushStringAnnotation(tag = "URL", annotation = "https://example.com/terms")
        withStyle(SpanStyle(color = Color.Gray, textDecoration = TextDecoration.Underline)) {
            append("이용약관")
        }
        pop()

        append(" 및 ")

        pushStringAnnotation(tag = "URL", annotation = "https://example.com/privacy")
        withStyle(SpanStyle(color = Color.Gray, textDecoration = TextDecoration.Underline)) {
            append("개인정보처리방침")
        }
        pop()

        append("에 동의하게 됩니다.")
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.fillMaxWidth(),
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        state = LoginState(),
        onAction = {}
    )
}
