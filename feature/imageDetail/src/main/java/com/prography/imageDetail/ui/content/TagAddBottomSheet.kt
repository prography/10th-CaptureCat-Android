package com.prography.imageDetail.ui.content

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prography.imageDetail.R
import com.prography.imageDetail.ui.contract.ImageDetailAction
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.ui.component.TagInputField
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiTagInfoChip
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Gray05
import com.prography.ui.theme.Gray09
import com.prography.ui.theme.Text01
import com.prography.ui.theme.headline03Bold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagAddBottomSheet(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onAction(ImageDetailAction.OnHideTagAddBottomSheet) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(Gray05.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                )
            }
        },
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = com.prography.ui.R.drawable.ic_close),
                        contentDescription = null,
                        tint = Gray09,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(24.dp)
                            .clickableWithoutRipple { onAction(ImageDetailAction.OnHideTagAddBottomSheet) }
                    )
                    Text(
                        text = "태그 추가",
                        style = headline03Bold,
                        color = Text01,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                TagInputField(
                    value = state.newTagText,
                    onValueChange = { onAction(ImageDetailAction.OnNewTagTextChange(it)) },
                    onDone = { onAction(ImageDetailAction.OnAddNewTag) },
                    errorMessage = state.tagErrorMessage
                )

                Spacer(Modifier.height(12.dp))
                // 선택 중 칩들
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

            UiBottomInputButton(
                text = "저장하기",
                enabled = state.newTagText.isNotBlank(),
                onClick = {
                    if (state.newTagText.isNotBlank()) onAction(ImageDetailAction.OnAddNewTag)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
