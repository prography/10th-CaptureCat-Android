package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prography.ui.theme.*

@Composable
fun UiBasicDialog(
    isVisible: Boolean,
    title: String = "",
    info: String,
    confirmButtonText: String = "확인",
    onConfirm: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onConfirm,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (title.isNotBlank()){
                        Text(
                            text = title,
                            style = headline02Bold,
                            color = Text01,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = info,
                        style = body02Regular,
                        color = Text02,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (confirmButtonText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        UiLabelAddButton (
                            text = confirmButtonText,
                            onClick = onConfirm,
                            modifier = Modifier.fillMaxWidth(),
                            size = ButtonSize.LARGE
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UiBasicDialogPreview() {
    UiBasicDialog(
        isVisible = true,
        title = "기본 다이어로그입니다",
        info = "이것은 기본 다이얼로그입니다.\n확인 버튼을 눌러 닫을 수 있습니다.",
        confirmButtonText = "확인",
        onConfirm = {}
    )
}

@Preview(showBackground = true)
@Composable
fun UiBasicNotButtonDialogPreview() {
    UiBasicDialog(
        isVisible = true,
        title = "기본 다이어로그입니다",
        info = "이것은 기본 다이얼로그입니다.\n확인 버튼을 눌러 닫을 수 있습니다.",
        confirmButtonText = "",
        onConfirm = {}
    )
}