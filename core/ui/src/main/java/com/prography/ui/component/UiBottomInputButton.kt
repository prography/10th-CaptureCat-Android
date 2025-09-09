package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.prography.ui.theme.*

enum class BottomInputButtonVariant { Primary, Sub }

@Composable
fun UiBottomInputButton(
    text: String,
    enabled: Boolean = true,
    variant: BottomInputButtonVariant = BottomInputButtonVariant.Primary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (variant) {
        BottomInputButtonVariant.Primary -> if (enabled) Primary else Gray04
        BottomInputButtonVariant.Sub -> Gray02
    }
    val contentColor = when (variant) {
        BottomInputButtonVariant.Primary -> if (enabled) White else Gray06
        BottomInputButtonVariant.Sub -> Text02
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(4.dp))
            .padding(vertical = 14.dp)
            .clickableWithoutRipple(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = headline03Bold, color = contentColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun UiBottomInputButtonPreview_Enabled() {
    Box(Modifier.padding(16.dp)) {
        UiBottomInputButton(text = "등록", enabled = true, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun UiBottomInputButtonPreview_Disabled() {
    Box(Modifier.padding(16.dp)) {
        UiBottomInputButton(text = "등록", enabled = false, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun UiBottomInputButtonPreview_Sub() {
    Box(Modifier.padding(16.dp)) {
        UiBottomInputButton(
            text = "등록",
            enabled = true,
            variant = BottomInputButtonVariant.Sub,
            onClick = {}
        )
    }
}
