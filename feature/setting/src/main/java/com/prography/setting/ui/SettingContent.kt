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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.setting.contract.SettingAction
import com.prography.setting.contract.SettingState
import com.prography.ui.component.SelectableCard
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.*
import com.prography.ui.R.string as UiString

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
                contentDescription = stringResource(id = UiString.setting_title),
                modifier = Modifier.clickable { onAction(SettingAction.OnNavigateUp) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(id = UiString.setting_title),
                style = headline02Bold,
                color = Text01
            )
        }

        if (!state.isLoggedIn) {
            MemberSettingContent(onAction = onAction)
        } else {
            GuestSettingContent(onAction = onAction)
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
    }
}

@Composable
private fun GuestSettingContent(onAction: (SettingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = stringResource(UiString.setting_guest_mode_message),
                    style = subhead01Bold,
                    color = Text01,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                UiPrimaryButton(
                    text = stringResource(UiString.setting_login_button),
                    onClick = { onAction(SettingAction.OnLogin) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_service_info))

        SettingMenuItem(text = stringResource(UiString.setting_privacy_policy)) {
            onAction(SettingAction.OnExternalLink("https://example.com/privacy"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_terms_of_service)) {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_version_info)) {}

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_help))

        SettingMenuItem(text = stringResource(UiString.setting_how_to_use)) {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
    }
}

@Composable
private fun MemberSettingContent(onAction: (SettingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Gray02, shape = RoundedCornerShape(12.dp))
        ) {
            Text(
                text = stringResource(UiString.setting_member_nickname, "캐치"),
                style = headline03Bold,
                color = Text01,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_service_info))

        SettingMenuItem(text = stringResource(UiString.setting_privacy_policy)) {
            onAction(SettingAction.OnExternalLink("https://example.com/privacy"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_terms_of_service)) {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }
        SettingMenuItem(text = stringResource(UiString.setting_version_info)) {}

        Spacer(modifier = Modifier.height(24.dp))

        SettingTitleMenuItem(text = stringResource(UiString.setting_help))

        SettingMenuItem(text = stringResource(UiString.setting_how_to_use)) {
            onAction(SettingAction.OnExternalLink("https://example.com/terms"))
        }

        SettingMenuItem(text = stringResource(UiString.setting_logout)) {
            onAction(SettingAction.OnClickLogout)
        }

        SettingMenuItem(text = stringResource(UiString.setting_withdraw)) {
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
