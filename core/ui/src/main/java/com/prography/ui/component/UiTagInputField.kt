package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.Error
import com.prography.ui.theme.Gray01
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Gray03
import com.prography.ui.theme.Gray09
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
    onClear: () -> Unit = {},
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = false
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    enabled = enabled,
                    textStyle = body02Regular.copy(
                        color = Text02
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (enabled && value.isNotBlank()) {
                                onDone()
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = if (enabled) Gray03 else Text03,
                                    style = body02Regular,
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.height(38.dp)
                    ) {
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
        }
    }
}


@Preview(name = "기본 상태", showBackground = false)
@Composable
fun TagInputPreview_Default() {
    TagInputField(
        value = "",
        onValueChange = {},
        onClear = {}
    )
}

@Preview(name = "입력 중 (포커스 있음)", showBackground = false)
@Composable
fun TagInputPreview_Typing() {
    TagInputField(
        value = "일상",
        onValueChange = {},
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
        onClear = {}
    )
}
