package com.prography.ui.component

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prography.ui.R
import com.prography.ui.theme.*
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.subhead02Bold

@Composable
fun UiNoticeDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    leftButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp, 32.dp, 24.dp, 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            style = headline02Bold,
                            color = Text01,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = message,
                            style = body02Regular,
                            color = Text02,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        UiLabelAddButton(
                            text = leftButtonText,
                            onClick = onConfirm,
                            modifier = Modifier.fillMaxWidth(),
                            size = ButtonSize.LARGE,
                            type = ButtonType.DEFAULT
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "다시 보지 않기",
                                color = Gray06,
                                style = caption02Regular,
                                modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                            Text(
                                text = "닫기",
                                color = Text03,
                                style = caption02Regular.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 10.dp, vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UiNoticeDialogPreview() {
    UiNoticeDialog(
        isVisible = true,
        title = "2026년 1월 28일\n" +
                "캡처캣 서비스가 종료됩니다",
        message = "그동안 캡처캣을 이용해주셔서 감사드리며, \n" +
                "서비스 종료에 관한 상세 내용은 공지사항을 통해 \n" +
                "확인해 주세요.",
        leftButtonText = "공지사항 확인하기",
        onDismiss = {},
        onConfirm = {}
    )
}
