package com.prography.imageDetail.ui.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prography.imageDetail.ui.contract.ImageDetailAction
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.ui.component.TagInputField
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Gray09
import com.prography.ui.theme.Text01
import com.prography.ui.theme.headline03Bold

@Composable
fun TagAddContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(Modifier.fillMaxWidth()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_close),
                contentDescription = null,
                tint = Gray09,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickableWithoutRipple { onAction(ImageDetailAction.ShowSheet(Sheet.Edit))  }
            )
            Text(
                text = "태그 추가",
                style = headline03Bold,
                color = Text01,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Input & selected chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TagInputField(
                value = state.newTagText,
                onValueChange = { onAction(ImageDetailAction.OnNewTagTextChange(it)) },
                onDone = {
                    if (state.newTagText.isNotBlank()) {
                        onAction(ImageDetailAction.OnAddNewTag)
                        focusManager.clearFocus()
                    }
                },
                errorMessage = state.tagErrorMessage
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp * 2 + 8.dp)
            ) {
                state.currentScreenshot?.tags?.forEach { t ->
                    UiTagSelectedChip(
                        text = t.name,
                        onDelete = { onAction(ImageDetailAction.OnTagDelete(t)) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        UiBottomInputButton(
            text = "저장하기",
            enabled = state.newTagText.isNotBlank(),
            onClick = {
                if (state.newTagText.isNotBlank()) onAction(ImageDetailAction.OnAddNewTag)
            },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding() // IME에만 반응
        )
    }
}