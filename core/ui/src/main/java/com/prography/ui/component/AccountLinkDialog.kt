package com.prography.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.prography.ui.R
import com.prography.ui.theme.*

@Composable
fun AccountLinkDialog(
    existingProvider: String,
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 제목
                    Text(
                        text = "기존에 사용하던 계정이 있어요",
                        style = headline03Bold,
                        color = Text01,
                        textAlign = TextAlign.Center
                    )

                    // 메시지
                    Text(
                        text = "이미 해당 이메일로 가입 되어 있어요." +
                                "카카오와 구글 계정을 하나로 통합할까요?",
                        style = body01Regular,
                        color = Text02,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 버튼들
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 취소 버튼
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Text03
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "취소",
                                style = body01Regular,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        // 연동 버튼
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "통합",
                                style = body01Regular,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getProviderDisplayName(provider: String): String {
    return when (provider.lowercase()) {
        "kakao" -> "카카오"
        "google" -> "구글"
        else -> provider
    }
}