package com.prography.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.prography.ui.theme.Text02
import com.prography.ui.theme.body02Regular

@Composable
fun UnderlinedClickableText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Text02,
    style: TextStyle = body02Regular
) {
    Text(
        text = text,
        style = style.merge(
            TextStyle(textDecoration = TextDecoration.Underline)
        ),
        color = color,
        modifier = modifier.clickable { onClick() }
    )
}
