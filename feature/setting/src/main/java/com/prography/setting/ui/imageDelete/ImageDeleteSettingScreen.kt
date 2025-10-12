// package com.prography.setting.ui.imageDelete

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.setting.ui.imageDelete.ImageDeleteSettingAction
import com.prography.setting.ui.imageDelete.ImageDeleteSettingEffect
import com.prography.setting.ui.imageDelete.ImageDeleteSettingViewModel
import com.prography.ui.R
import com.prography.ui.component.CustomSwitch
import com.prography.ui.component.UiHeader
import com.prography.ui.theme.*

@Composable
fun ImageDetailSettingScreen(
) {

    val viewModel: ImageDeleteSettingViewModel = hiltViewModel()

    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // 최초 로드
    LaunchedEffect(Unit) { viewModel.sendAction(ImageDeleteSettingAction.Load) }

    // 효과 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is ImageDeleteSettingEffect.NavigateUp -> viewModel.sendAction(ImageDeleteSettingAction.ClickBack)
                is ImageDeleteSettingEffect.ShowToast -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        UiHeader(
            title = stringResource(id = R.string.image_delete_setting_title),
            showBackButton = true
        ) { viewModel.sendAction(ImageDeleteSettingAction.ClickBack) }

        // 상단 간격
        Spacer(Modifier.height(8.dp))

        // 토글 섹션
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.image_delete_setting_toggle_title),
                    style = body01Regular,
                    color = Text01
                )
                CustomSwitch(
                    checked = state.isEnabled,
                    onCheckedChange = { viewModel.sendAction(ImageDeleteSettingAction.Toggle(it)) },
                    enabled = !state.isLoading
                )
            }

            Text(
                text = stringResource(R.string.image_delete_setting_toggle_desc), // "캡처캣에 업로드가 완료되면, 갤러리에서 이미지를 삭제할지 팝업으로 안내해요."
                style = body02Regular,
                color = Text03
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Divider, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // 하단 주석 두 줄
            Text(
                text = "· 설정 후에도 삭제는 매번 직접 선택할 수 있어요.",
                style = caption02Regular,
                color = Text03
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "· 삭제된 이미지는 ‘휴지통’ 또는 ‘최근 삭제된 항목’에 30일간 보관돼요.",
                style = caption02Regular,
                color = Text03
            )
        }
    }
}
