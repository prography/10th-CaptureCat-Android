package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UiUnderlinedTextButton(
    fullText: String,
    underlineTarget: String,
    fontSize: TextUnit = 18.sp,
    color: Color = Color(0xFF999999),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotated = remember(fullText, underlineTarget) {
        buildAnnotatedString {
            val start = fullText.indexOf(underlineTarget)
            if (start >= 0) {
                append(fullText.substring(0, start))
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(underlineTarget)
                }
                append(fullText.substring(start + underlineTarget.length))
            } else {
                append(fullText)
            }
        }
    }

    Text(
        text = annotated,
        fontSize = fontSize,
        color = color,
        modifier = modifier.clickable { onClick() }
    )
}
