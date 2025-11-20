package com.prography.auth.route.screen.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
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
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UnderlinedClickableText
import com.prography.ui.theme.Primary
import com.prography.ui.theme.caption02Regular
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
            text = stringResource(R.string.common_later),
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
                KakaoLoginButton(
                    isRecent = state.recentLoginProvider == com.prography.domain.model.LoginProvider.KAKAO,
                    onClick = { onAction(LoginAction.ClickKakao) }
                )
                GoogleLoginButton(
                    isRecent = state.recentLoginProvider == com.prography.domain.model.LoginProvider.GOOGLE,
                    onClick = { onAction(LoginAction.ClickGoogle) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                AgreementText()
            }
        }
    }

    UiCommonDialog(
        title = stringResource(R.string.dialog_existing_account_title),
        message = stringResource(
            R.string.dialog_existing_account_message,
            getProviderDisplayName(state.pendingLink?.existingProvider ?: ""),
            getProviderDisplayName(state.pendingAuth?.provider ?: "")
        ),
        leftButtonText = stringResource(R.string.dialog_close),
        rightButtonText = stringResource(R.string.dialog_link_account),
        onDismiss = { onAction(LoginAction.AccountLinkDismiss) },
        onConfirm = {  onAction(LoginAction.AccountLinkConfirm) },
        isVisible = state.isLinkDialogVisible
    )
}

@Composable
private fun RecentBadge(modifier: Modifier) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🟧 상단 네모
        Box(
            modifier = Modifier
                .background(Primary, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.5.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.dialog_recent_login), color = Color.White, style = caption02Regular)
        }

        val TriangleShape = GenericShape { size, _ ->
            val w = size.width
            val h = size.height
            val r = 4.dp.value

            moveTo(0f, 0f)
            lineTo(w / 2f - r, h - r)
            quadraticTo(
                w / 2f, h,
                w / 2f + r, h - r
            )
            lineTo(w, 0f)
            close()
        }

        Box(
            modifier = Modifier
                .offset(y = (-1).dp)
                .size(width = 12.dp, height = 8.dp)
                .background(Primary, TriangleShape)
        )
    }
}


@Composable
fun KakaoLoginButton(isRecent: Boolean, onClick: () -> Unit) {
    Box {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500)),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painterResource(R.drawable.ic_kakao_login), contentDescription = stringResource(R.string.cd_kakao_login_icon), modifier = Modifier.size(18.dp))
                Text(text = stringResource(R.string.login_kakao), color = Color.Black, style = subhead01Bold)
            }
        }
        if (isRecent) {
            RecentBadge(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-16).dp, y = (-24).dp)
            )
        }
    }
}

@Composable
fun GoogleLoginButton(isRecent: Boolean, onClick: () -> Unit) {
    Box {
        Button(
            onClick = onClick,
            modifier = Modifier
                .border(1.dp, Color(0xFF747775), RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painterResource(R.drawable.ic_google_login), contentDescription = stringResource(R.string.cd_google_login_icon), modifier = Modifier.size(18.dp))
                Text(text = stringResource(R.string.login_google), color = Color.Black, style = subhead01Bold)
            }
        }
        if (isRecent) {
            RecentBadge(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-16).dp, y = (-24).dp)
            )
        }
    }
}

@Composable
private fun getProviderDisplayName(provider: String): String {
    return when (provider.lowercase()) {
        "kakao" -> stringResource(R.string.provider_kakao)
        "google" -> stringResource(R.string.provider_google)
        "apple" -> stringResource(R.string.provider_apple)
        else -> provider
    }
}

@Composable
fun AgreementText() {
    val context = LocalContext.current

    val termsText = stringResource(R.string.login_terms)
    val privacyText = stringResource(R.string.login_privacy)

    val annotatedText = buildAnnotatedString {
        val agreementText = stringResource(R.string.login_agreement, termsText, privacyText)

        // Split the text to find where to apply annotations
        val parts = agreementText.split(termsText, privacyText, ignoreCase = true)

        if (parts.size >= 3) {
            append(parts[0])

            pushStringAnnotation(
                tag = "URL",
                annotation = "https://ujins.notion.site/1ff6b91b83f580519258d2256a319737"
            )
            withStyle(SpanStyle(color = Color.Gray, textDecoration = TextDecoration.Underline)) {
                append(termsText)
            }
            pop()

            append(parts[1])

            pushStringAnnotation(
                tag = "URL",
                annotation = "https://ujins.notion.site/1ff6b91b83f58081abb1e90909cce9fd"
            )
            withStyle(SpanStyle(color = Color.Gray, textDecoration = TextDecoration.Underline)) {
                append(privacyText)
            }
            pop()

            append(parts[2])
        } else {
            // Fallback if string formatting doesn't work as expected
            append(agreementText)
        }
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
