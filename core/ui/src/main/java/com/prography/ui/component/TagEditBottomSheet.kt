package com.prography.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Text01
import com.prography.ui.theme.headline03Bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagEditBottomSheet(
    tagId: Long,
    initialText: String,
    onSubmit: (Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf(initialText) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0),
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 28.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "태그 수정" /*stringResource(com.prography.ui.R.string.common_edit)*/, // "수정"
                    style = headline03Bold,
                    color = Text01
                )
                Icon(
                    painter = painterResource(id = com.prography.ui.R.drawable.ic_bottom_close),
                    contentDescription = stringResource(com.prography.ui.R.string.common_close),
                    tint = Text01,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithoutRipple { onDismiss() }
                )
            }

            TagInputField(
                value = text,
                onValueChange = { text = it },
                onClear = { text = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
            )

            UiBottomInputButton(
                text = stringResource(com.prography.ui.R.string.common_complete), // "완료"
                enabled = text.isNotBlank() && text != initialText,
                onClick = { if (text.isNotBlank()) onSubmit(tagId, text.trim()) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
