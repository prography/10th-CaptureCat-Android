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
        "캡쳐캣을 사용하기 어려움.",
        "개인정보가 우려됨.",
        "캡쳐캣이 더 이상 유용하지 않음.",
        "이미지 파일이 안전하지 않다고 생각됨."
    )

    UiBasicDialog(
        isVisible = state.showWithdrawDialog,
        title = "회원탈퇴 완료",
        info = "그동안 이용해주셔서 감사합니다.\n" +
                "다음에도 이용해주세요!",
        confirmButtonText = "확인",
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
            contentDescription = "Back",
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clickableWithoutRipple {
                    viewModel.sendAction(WithdrawAction.Cancel)
                }
        )
        Text(
            text = "삭제하기 전에 도움을 받아보세요.",
            style = headline02Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "그동안 이용해주셔서 감사합니다. 계정을 삭제하는 이유를 알려주시면 해당 문제에 관해 저희가 도움을 드릴 수 있습니다. 원치않으시는 경우 이유를 선택하지 않고 삭제를 계속 진행하실 수 있습니다.",
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
            text = "계속",
            fontSize = 14.sp,
            onClick = { viewModel.sendAction(WithdrawAction.ClickContinue) },
            state = if (state.selectedReason != null) ButtonState.Enabled else ButtonState.Disabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        UiLabelAddButton(
            text = "취소",
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