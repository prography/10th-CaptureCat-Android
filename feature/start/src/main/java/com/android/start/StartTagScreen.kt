package com.android.start

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.navigation.NavigationHelper
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiCommonDialog
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.UiTagChip
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Gray06
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PrimaryPress
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.subhead02Bold
import com.prography.util.MixpanelUtil

@Composable
fun StartTagScreen(
    tagOptions: List<String> = listOf(
        "쇼핑", "직무 관련", "레퍼런스", "코디",
        "공부", "글귀", "여행", "자기계발",
        "맛집", "노래", "레시피", "운동"
    ),
    onFinishSelection: (List<String>) -> Unit,
    onNavigateBack: () -> Unit = {},
    viewModel: StartTagViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(Unit) {
        MixpanelUtil.track("view_start")
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            // 📍 상단 텍스트 영역
            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(com.prography.ui.R.string.start_tag_question),
                    style = headline02Bold,
                    color = Text01
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        com.prography.ui.R.string.start_tag_info,
                        state.maxSelectableTags
                    ),
                    style = body02Regular,
                    color = Text03
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tagOptions.forEach { tag ->
                    val isSelected = tag in state.selectedTags
                    UiTagChip(
                        text = tag,
                        isSelected = isSelected,
                        onClick = {
                            viewModel.sendAction(StartTagAction.ToggleTag(tag))
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 26.dp)
        ) {
            val buttonState = remember(state.selectedTags) {
                if (state.selectedTags.isEmpty()) ButtonState.Disabled
                else ButtonState.Enabled
            }

            UiPrimaryButton(
                text = stringResource(
                    com.prography.ui.R.string.start_tag_complete,
                    state.selectedTags.size,
                    state.maxSelectableTags
                ),
                onClick = {
                    viewModel.sendAction(StartTagAction.SaveSelectedTags(state.selectedTags))
                    onFinishSelection(state.selectedTags)
                },
                state = buttonState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    UiCommonDialog(
        isVisible = showExitDialog,
        title = stringResource(com.prography.ui.R.string.common_exit_dialog_title),
        message = stringResource(com.prography.ui.R.string.start_tag_exit_dialog_message),
        leftButtonText = stringResource(com.prography.ui.R.string.common_continue),
        rightButtonText = stringResource(com.prography.ui.R.string.common_exit),
        onDismiss = { showExitDialog = false },
        onConfirm = {
            showExitDialog = false
            onNavigateBack()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StartTagScreenPreview() {
    StartTagScreen(
        tagOptions = listOf(
            "쇼핑", "직무 관련", "레퍼런스", "코디",
            "공부", "글귀", "여행", "자기계발",
            "맛집", "노래", "레시피", "운동"
        ),
        onFinishSelection = { }
    )
}
