package com.prography.home.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.ButtonType
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.theme.Gray05
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.White
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.caption02Regular
import com.prography.ui.theme.headline02Bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteChoiceBottomSheet(
    onDeleteNow: () -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Black.copy(alpha = 0.40f),
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
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        DeleteChoiceBottomSheetContent(
            onDeleteNow = onDeleteNow,
            onLater = onDismiss
        )
    }
}


@Composable
private fun DeleteChoiceBottomSheetContent(
    onDeleteNow: () -> Unit = {},
    onLater: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .background(White)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append("업로드 된 캡처를 ")
                withStyle(style = SpanStyle(color = Primary)) {
                    append("갤러리에서 삭제")
                }
                append("할까요?")
            },
            style = headline02Bold,
            color = Text01
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "중복 이미지는 정리하고, 갤러리를 더 깔끔하게 정리할 수 있어요. 삭제 여부는 매번 선택할 수 있어요.",
            style = body02Regular,
            color = Text01,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(26.dp))

        UiLabelAddButton(
            onClick = onDeleteNow,
            text = "삭제 설정하기",
            size = ButtonSize.LARGE,
            type = ButtonType.DEFAULT,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        UiLabelAddButton(
            onClick = onLater,
            text = "나중에",
            size = ButtonSize.LARGE,
            type = ButtonType.SUB,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = "설정에서 언제든지 변경할 수 있어요",
            style = caption02Regular,
            color = Text03
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteChoiceBottomSheetContent() {
    androidx.compose.material3.Surface {
        DeleteChoiceBottomSheetContent()
    }
}
