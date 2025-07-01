package com.prography.setting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.prography.setting.contract.SettingAction
import com.prography.setting.contract.SettingState
import com.prography.ui.component.SelectableCard
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*

@Composable
fun SettingContent(
    state: SettingState,
    onAction: (SettingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_backward),
                contentDescription = "뒤로가기",
                modifier = Modifier.clickable { onAction(SettingAction.OnNavigateUp) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "설정",
                style = headline02Bold,
                color = Text01
            )
        }

        if (!state.isLoggedIn) {
            // 회원 모드
            MemberSettingContent(onAction = onAction)
        } else {
            // 게스트 모드
            GuestSettingContent(onAction = onAction)
        }

        if (state.showLogoutDialog) {
            UiCommonDialog(
                isVisible = true,
                title = "로그아웃하면",
                message = "스크린샷을 관리 못할 수 있어요.\n그래도 로그아웃하시겠어요?",
                leftButtonText = "취소",
                rightButtonText = "로그아웃",
                onDismiss = { onAction(SettingAction.DismissLogoutDialog) },
                onConfirm = { onAction(SettingAction.OnLogout) }
            )
        }

        if (state.showWithdrawDialog) {
            UiCommonDialog(
                isVisible = true,
                title = "회원탈퇴하면",
                message = "캡쳐캣에 쌓인 유저님의 모든 데이터가 삭제됩니다.\n그래도 회원탈퇴하시겠어요?",
                leftButtonText = "취소",
                rightButtonText = "회원탈퇴",
                onDismiss = { onAction(SettingAction.DismissWithdrawDialog) },
                onConfirm = { onAction(SettingAction.OnNavigateToWithdraw) }
            )
        }
    }
}

@Composable
private fun GuestSettingContent(onAction: (SettingAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 로그인 유도 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Gray01)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "현재 게스트 모드로 사용하고 있어요",
                    style = subhead01Bold,
                    color = Text01,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                UiPrimaryButton(
                    text = "로그인하기",
                    onClick = { onAction(SettingAction.OnLogin) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = "서비스 정보")

        SettingMenuItem(text = "개인정보 처리 방침") {
            onAction(SettingAction.OnExternalLink("https://example.com/privacy"))
        }
        SettingMenuItem(text = "서비스 이용약관") {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
        SettingMenuItem(text = "버전 정보") {

        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = "도움말")

        SettingMenuItem(text = "서비스 이용방법") {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
    }
}

@Composable
private fun MemberSettingContent(
    onAction: (SettingAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Gray02, shape = RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "캐치님",
                style = headline03Bold,
                color = Text01,
                modifier = Modifier.padding(24.dp) // 내부 여백
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = "서비스 정보")

        SettingMenuItem(text = "개인정보 처리 방침") {
            onAction(SettingAction.OnExternalLink("https://example.com/privacy"))
        }
        SettingMenuItem(text = "서비스 이용약관") {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
        SettingMenuItem(text = "버전 정보") {

        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = "도움말")

        SettingMenuItem(text = "서비스 이용방법") {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }

        SettingMenuItem(text = "로그아웃") {
            onAction(SettingAction.OnClickLogout)
        }

        SettingMenuItem(text = "회원탈퇴") {
            onAction(SettingAction.OnClickWithdraw)
        }
    }
}

@Composable
private fun SettingMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = body01Regular,
        color = Text01,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
private fun SettingTitleMenuItem(
    text: String
) {
    Text(
        text = text,
        style = body02Regular,
        color = Text02,
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray02)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingContentGuestPreview() {
    SettingContent(
        state = SettingState(isLoggedIn = false),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SettingContentMemberPreview() {
    SettingContent(
        state = SettingState(isLoggedIn = true),
        onAction = {}
    )
}