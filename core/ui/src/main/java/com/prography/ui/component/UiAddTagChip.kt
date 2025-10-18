package com.prography.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Divider
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body02Regular

@Composable
fun UiAddTagChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Gray02,
        border = BorderStroke(1.dp, Divider),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier
                .clickableWithoutRipple(onClick = onClick)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, style = body02Regular, color = Text01)
        }
    }
}
