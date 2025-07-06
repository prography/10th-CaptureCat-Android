package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.caption01SemiBold

@Composable
fun UiTagInfoChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = caption01SemiBold,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(Color(0x66000000), RoundedCornerShape(4.5.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}