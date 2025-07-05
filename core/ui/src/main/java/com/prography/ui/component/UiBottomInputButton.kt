package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Gray06
import com.prography.ui.theme.Primary
import com.prography.ui.theme.White
import com.prography.ui.theme.headline03Bold

@Composable
fun UiBottomInputButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(
                color = if (enabled) Primary else Gray04,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(vertical = 14.dp)
            .clickableWithoutRipple(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = headline03Bold,
            color = if (enabled) White else Gray06
        )
    }
}
