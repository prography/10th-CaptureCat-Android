package com.prography.setting.ui.withdraw

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.setting.ui.withdraw.WithdrawAction
import com.prography.setting.ui.withdraw.WithdrawEffect
import com.prography.setting.ui.withdraw.WithdrawViewModel
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.ButtonState
import com.prography.ui.component.ButtonType
import com.prography.ui.component.SelectableCard
import com.prography.ui.component.UiBasicDialog
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

@Composable
fun WithdrawScreen(
    onNavigateBack: () -> Unit,
    onWithdrawComplete: () -> Unit
) {
    val viewModel: WithdrawViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is WithdrawEffect.NavigateToLogin -> onWithdrawComplete()
                is WithdrawEffect.NavigateUp -> onNavigateBack()
            }
        }
    }

    val reasons = listOf(
        stringResource(com.prography.ui.R.string.withdraw_reason_1),
        stringResource(com.prography.ui.R.string.withdraw_reason_2),
        stringResource(com.prography.ui.R.string.withdraw_reason_3),
        stringResource(com.prography.ui.R.string.withdraw_reason_4)
    )

    UiBasicDialog(
        isVisible = state.showWithdrawDialog,
        title = stringResource(com.prography.ui.R.string.withdraw_dialog_title),
        info = stringResource(com.prography.ui.R.string.withdraw_dialog_info),
        confirmButtonText = stringResource(com.prography.ui.R.string.common_confirm),
        onConfirm = { viewModel.sendAction(WithdrawAction.ConfirmWithdraw) }
    )

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(com.prography.ui.R.drawable.ic_arrow_backward),
            contentDescription = stringResource(com.prography.ui.R.string.common_back),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clickableWithoutRipple {
                    viewModel.sendAction(WithdrawAction.Cancel)
                }
        )
        Text(
            text = stringResource(com.prography.ui.R.string.withdraw_reason_title),
            style = headline02Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(com.prography.ui.R.string.withdraw_reason_info),
            style = body02Regular,
            color = Text03
        )
        Spacer(modifier = Modifier.height(56.dp))

        // 이유 선택 리스트
        reasons.forEach { reason ->
            SelectableCard(
                text = reason,
                selected = state.selectedReason == reason,
                onClick = { viewModel.sendAction(WithdrawAction.SelectReason(reason)) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        UiPrimaryButton(
            text = stringResource(com.prography.ui.R.string.common_continue),
            fontSize = 14.sp,
            onClick = { viewModel.sendAction(WithdrawAction.ClickContinue) },
            state = if (state.selectedReason != null) ButtonState.Enabled else ButtonState.Disabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        UiLabelAddButton(
            text = stringResource(com.prography.ui.R.string.common_cancel),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            size = ButtonSize.LARGE,
            type = ButtonType.SUB,
            onClick = { viewModel.sendAction(WithdrawAction.Cancel) }
        )
    }
}

@Preview(showBackground = true, name = "WithdrawScreen - 선택되지 않음")
@Composable
fun Preview_WithdrawScreen_Unselected() {
    WithdrawScreen(
        onNavigateBack = {},
        onWithdrawComplete = {}
    )
}