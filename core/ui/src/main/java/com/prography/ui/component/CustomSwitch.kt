package com.prography.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Gray06
import com.prography.ui.theme.Primary

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val trackWidth = 51.dp
    val trackHeight = 31.dp
    val thumbDiameter = 27.dp
    val innerPadding = 2.dp // 좌우 여백

    val thumbX by animateDpAsState(
        targetValue = if (checked)
            trackWidth - thumbDiameter - innerPadding
        else
            innerPadding,
        label = "thumbX"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) Primary else Gray06,
        label = "trackColor"
    )

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .clickableWithoutRipple(enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        // 👍 그림자 -> 배경(원) 순서
        Box(
            modifier = Modifier
                .offset(x = thumbX)
                .size(thumbDiameter)
                .shadow(elevation = 4.dp, shape = CircleShape, clip = false)
                .background(Color.White, CircleShape)
        )
    }
}