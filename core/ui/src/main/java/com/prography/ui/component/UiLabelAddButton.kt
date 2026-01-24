// ui/component/Buttons.kt
package com.prography.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.theme.*

/* ---------- 공통 타입 ---------- */
enum class ButtonSize { LARGE, MEDIUM, SMALL }
enum class ButtonType { DEFAULT, SUB }
enum class ButtonState { Enabled, Pressed, Disabled, Loading }

/* ---------- 사이즈/패딩 ---------- */
private fun paddingFor(size: ButtonSize): PaddingValues = when (size) {
    ButtonSize.LARGE  -> PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ButtonSize.MEDIUM -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ButtonSize.SMALL  -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
}

/* ---------- 색상 정책 ---------- */
private data class ButtonPalette(
    val container: Color,
    val content: Color,
    val indicator: Color, // 로딩 인디케이터 색
)

private fun paletteFor(type: ButtonType, state: ButtonState): ButtonPalette {
    return when (type) {
        ButtonType.DEFAULT -> when (state) {
            ButtonState.Enabled -> ButtonPalette(Primary, Color.White, Color.White)
            ButtonState.Pressed -> ButtonPalette(PrimaryPress, Color.White, Color.White)
            ButtonState.Disabled -> ButtonPalette(Gray04, Gray06, Gray06)
            ButtonState.Loading -> ButtonPalette(Primary, Color.White, Color.White)
        }
        ButtonType.SUB -> when (state) {
            ButtonState.Enabled -> ButtonPalette(Gray02, Text03, Text03)
            ButtonState.Pressed -> ButtonPalette(Gray03, Text03, Text03)
            ButtonState.Disabled -> ButtonPalette(Gray04, Gray06, Gray06)
            ButtonState.Loading -> ButtonPalette(Gray02, Text03, Text03)
        }
    }
}

/* ---------- 공통 버튼 베이스 ---------- */
@Composable
private fun BaseButton(
    text: String,
    modifier: Modifier = Modifier,
    type: ButtonType,
    size: ButtonSize,
    state: ButtonState,
    fontSize: TextUnit,
    onClick: (() -> Unit)? // nullable 로 선택적 클릭 허용
) {
    val palette = paletteFor(type, state)
    val enabled = state != ButtonState.Disabled && state != ButtonState.Loading
    Button(
        onClick = { if (enabled) onClick?.invoke() },
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = paddingFor(size),
        colors = ButtonDefaults.buttonColors(containerColor = palette.container)
    ) {
        if (state == ButtonState.Loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(5.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = palette.indicator,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else {
            Text(
                text = text,
                style = subhead02Bold.copy(color = palette.content, fontSize = fontSize)
            )
        }
    }
}

/* ---------- 라벨형(작은 CTA) ---------- */
@Composable
fun UiLabelAddButton(
    text: String,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.MEDIUM,
    type: ButtonType = ButtonType.DEFAULT,
    state: ButtonState = ButtonState.Enabled,
    onClick: (() -> Unit)? = null, // 선택적 onClick
) {
    BaseButton(
        text = text,
        modifier = modifier,
        type = type,
        size = size,
        state = state,
        fontSize = 14.sp,
        onClick = onClick
    )
}

/* ---------- 메인 프라이머리 버튼 ---------- */
@Composable
fun UiPrimaryButton(
    text: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    state: ButtonState = ButtonState.Enabled,
    fontSize: TextUnit = 18.sp
) {
    BaseButton(
        text = text,
        modifier = modifier,
        type = ButtonType.DEFAULT,
        size = ButtonSize.LARGE,
        state = state,
        fontSize = fontSize,
        onClick = onClick
    )
}
