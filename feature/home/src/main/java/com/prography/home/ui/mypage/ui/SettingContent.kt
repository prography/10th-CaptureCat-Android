package com.prography.home.ui.mypage.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.home.R
import com.prography.home.ui.mypage.contract.SettingAction
import com.prography.home.ui.mypage.contract.SettingState
import com.prography.ui.component.SelectableCard
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UiHeader
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*
import com.prography.ui.R.string as UiString
import androidx.core.net.toUri

@Composable
fun SettingContent(
    state: SettingState,
    onAction: (SettingAction) -> Unit
) {
    val context = LocalContext.current
    val versionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "-"
        } catch (e: Exception) {
            "-"
        }
    }
    val onUpdateClick = remember { { openPlayStore(context) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        UiHeader(
            title = stringResource(id = UiString.setting_title),
            showBackButton = true
        )
        if (state.isLoggedIn) {
            MemberSettingContent(
                nickname = state.nickname ?: "사용자",
                email = state.email ?: "",
                onAction = onAction,
                versionName = versionName
            )
        } else {
            GuestSettingContent(onAction = onAction, versionName = versionName)
        }

        if (state.showLogoutDialog) {
            UiCommonDialog(
                isVisible = true,
                title = stringResource(UiString.setting_logout_dialog_title),
                message = stringResource(UiString.setting_logout_dialog_message),
                leftButtonText = stringResource(UiString.setting_cancel),
                rightButtonText = stringResource(UiString.setting_logout),
                onDismiss = { onAction(SettingAction.DismissLogoutDialog) },
                onConfirm = { onAction(SettingAction.OnLogout) }
            )
        }

        if (state.showWithdrawDialog) {
            UiCommonDialog(
                isVisible = true,
                title = stringResource(UiString.setting_withdraw_dialog_title),
                message = stringResource(UiString.setting_withdraw_dialog_message),
                leftButtonText = stringResource(UiString.setting_cancel),
                rightButtonText = stringResource(UiString.setting_withdraw),
                onDismiss = { onAction(SettingAction.DismissWithdrawDialog) },
                onConfirm = { onAction(SettingAction.OnNavigateToWithdraw) }
            )
        }
        UiCommonDialog(
            isVisible = state.showResetDialog,
            title = stringResource(UiString.setting_reset_dialog_title),
            message = stringResource(UiString.setting_reset_dialog_message),
            leftButtonText = stringResource(UiString.setting_cancel),
            rightButtonText = stringResource(UiString.setting_ok),
            onDismiss = { onAction(SettingAction.DismissResetDialog) },
            onConfirm = { onAction(SettingAction.OnReset) }
        )
    }
}

@Composable
private fun GuestSettingContent(
    onAction: (SettingAction) -> Unit,
    versionName: String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryLow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(UiString.setting_guest_mode_message),
                    style = subhead01Bold,
                    color = Text02,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(UiString.setting_login_device_info),
                    style = caption02Regular,
                    color = Text02,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                UiPrimaryButton(
                    text = stringResource(UiString.setting_login_button),
                    onClick = { onAction(SettingAction.OnLogin) },
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_service_info))

        SettingMenuItem(text = stringResource(UiString.setting_privacy_policy)) {
            onAction(SettingAction.OnExternalLink("https://ujins.notion.site/1ff6b91b83f58081abb1e90909cce9fd"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_terms_of_service)) {
            onAction(SettingAction.OnExternalLink("https://ujins.notion.site/1ff6b91b83f580519258d2256a319737"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_app_review)) {

        }
        SettingMenuItem(
            text = stringResource(UiString.setting_version_info),
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = versionName,
                        style = caption02Regular,
                        color = Text03
                    )
                    Spacer(Modifier.width(8.dp))
                    UpdateBadge(onClick = {}) // 마켓 이동 등
                }
            },
            onClick = {}
        )
        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_help))

        SettingMenuItem(text = stringResource(UiString.setting_screenshot_reset)) {
            onAction(SettingAction.OnClickReset)
        }
    }
}

@Composable
private fun MemberSettingContent(
    nickname: String,
    email: String,
    onAction: (SettingAction) -> Unit,
    versionName: String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Gray02, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(UiString.setting_member_nickname, nickname),
                style = headline03Bold,
                color = Text01,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = email,
                style = body02Regular,
                color = Text03
            )
        }

        SettingTitleMenuItem(text = stringResource(UiString.setting_user_preferences))
        SettingMenuItem(text = stringResource(UiString.setting_tag_settings)) {

        }
        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_service_info))

        SettingMenuItem(text = stringResource(UiString.setting_privacy_policy)) {
            onAction(SettingAction.OnExternalLink("https://ujins.notion.site/1ff6b91b83f58081abb1e90909cce9fd"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_terms_of_service)) {
            onAction(SettingAction.OnExternalLink("https://ujins.notion.site/1ff6b91b83f580519258d2256a319737"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_app_review)) {

        }
        SettingMenuItem(
            text = stringResource(UiString.setting_version_info),
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = versionName,
                        style = caption02Regular,
                        color = Text03
                    )
                    Spacer(Modifier.width(8.dp))
                    UpdateBadge(onClick = {})
                }
            },
            onClick = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_help))

        SettingMenuItem(text = stringResource(UiString.setting_channel_inquiry)) {

        }

        SettingMenuWithdrawItem(text = stringResource(UiString.setting_logout)) {
            onAction(SettingAction.OnClickLogout)
        }

        SettingMenuWithdrawItem(text = stringResource(UiString.setting_withdraw)) {
            onAction(SettingAction.OnClickWithdraw)
        }
    }
}

@Composable
private fun SettingMenuItem(
    text: String,
    trailing: @Composable (() -> Unit)? = {
        Icon(
            painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_forward), // 오른쪽 화살표 아이콘
            contentDescription = null,
            tint = Text03
        )
    },
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != {}, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = body01Regular,
            color = Text01
        )
        if (trailing != null) trailing()
    }
}

@Composable
private fun SettingMenuWithdrawItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = caption02Regular,
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

@Composable
fun UpdateBadge(
    text: String = stringResource(UiString.setting_update),
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, PrimaryPress, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = subhead03Bold, color = Primary)
    }
}

// utils
private fun openPlayStore(context: Context, packageName: String = context.packageName) {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri()
            ).setPackage("com.android.vending")
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            )
        )
    }
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
        state = SettingState(isLoggedIn = true, nickname = "테스트", "aaa@aaa.aaa"),
        onAction = {}
    )
}
