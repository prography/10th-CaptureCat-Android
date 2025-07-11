package com.prography.organize.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prography.ui.component.TagInputField
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.caption02Regular
import com.prography.ui.theme.headline03Bold
import com.prography.ui.theme.subhead02Bold
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagAddBottomSheet(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val isOldAndroid = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S // S == API 31 == Android 12

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0),
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 28.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "태그 추가",
                        style = headline03Bold,
                        color = Text01
                    )

                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            painter = painterResource(id = com.prography.ui.R.drawable.ic_bottom_close), // 아이콘 리소스 맞게 수정
                            contentDescription = "닫기",
                            tint = Text01,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                TagInputField(
                    value = text,
                    onValueChange = { text = it },
                    onClear = { text = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(top = 20.dp, bottom = 32.dp , start = 16.dp, end = 16.dp)
                )

                UiBottomInputButton(
                    text = "완료",
                    enabled = text.isNotBlank(),
                    onClick = {
                        if (text.isNotBlank()) onAdd(text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (isOldAndroid) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    )
                }
            }
        }
    }
}
