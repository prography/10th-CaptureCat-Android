package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.Error
import com.prography.ui.theme.Gray01
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.caption02Regular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "추가할 태그를 입력해주세요",
    errorMessage: String? = null,
    counterText: String = "",
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isError = errorMessage != null

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .background(
                    color = Gray01,
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> Error
                        else -> Gray03
                    },
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = body02Regular.copy(color = Text02),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Text03,
                                    style = body02Regular
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                if (value.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_text_field_delete),
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = Error,
                    style = caption02Regular
                )
            }

            Text(
                text = counterText,
                color = Text02,
                style = body02Regular
            )
        }
    }
}

@Preview(name = "기본 상태", showBackground = false)
@Composable
fun TagInputPreview_Default() {
    TagInputField(
        value = "",
        onValueChange = {},
        counterText = "0/10",
        onClear = {}
    )
}

@Preview(name = "입력 중 (포커스 있음)", showBackground = false)
@Composable
fun TagInputPreview_Typing() {
    TagInputField(
        value = "일상",
        onValueChange = {},
        counterText = "1/10",
        onClear = {}
    )
}

@Preview(name = "중복 태그 에러", showBackground = false)
@Composable
fun TagInputPreview_Error() {
    TagInputField(
        value = "여행",
        onValueChange = {},
        errorMessage = "동일한 태그가 이미 존재합니다.",
        counterText = "3/10",
        onClear = {}
    )
}
