package com.prography.setting.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.component.ButtonState
import com.prography.ui.component.SelectableCard
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold

@Composable
fun WithdrawScreen(
    onNavigateBack: () -> Unit,
    onWithdrawComplete: () -> Unit
) {
    val reasons = listOf(
        "캡쳐캣을 사용하기 어려움.",
        "개인정보가 우려됨.",
        "캡쳐캣이 더 이상 유용하지 않음.",
        "이미지 파일이 안전하지 않다고 생각됨."
    )

    var selectedReason by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Text(
            text = "삭제하기 전에 도움을 받아보세요.",
            style = headline02Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "그동안 이용해주셔서 감사합니다. 제품을 삭제하는 이유를 알려주시면 해당 문제에 대해 저희가 도움을 드릴 수 있습니다.\n원치 않으실 경우 이유를 선택하지 않고 삭제를 계속 진행하실 수 있습니다.",
            style = body02Regular,
            color = Text03
        )
        Spacer(modifier = Modifier.height(60.dp))

        // 이유 선택 리스트
        reasons.forEach { reason ->
            SelectableCard(
                text = reason,
                selected = selectedReason == reason,
                onClick = { selectedReason = reason },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        UiPrimaryButton(
            text = "계속",
            onClick = { onWithdrawComplete() },
            state = if (selectedReason != null) ButtonState.Enabled else ButtonState.Disabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "취소",
            modifier = Modifier
                .clickable { onNavigateBack() }
                .align(Alignment.CenterHorizontally),
            color = Text02
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