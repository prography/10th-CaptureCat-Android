package com.prography.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
fun UiCommonDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    leftButtonText: String,
    rightButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            style = headline02Bold,
                            color = Text01,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = message,
                            style = body02Regular,
                            color = Text02,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(
                                        color = Gray02,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    text = leftButtonText,
                                    style = subhead02Bold,
                                    color = Text03,
                                    textAlign = TextAlign.Center
                                )
                            }

                            TextButton(
                                onClick = onConfirm,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(
                                        color = Primary,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Text(
                                    text = rightButtonText,
                                    style = subhead02Bold,
                                    color = PureWhite,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
