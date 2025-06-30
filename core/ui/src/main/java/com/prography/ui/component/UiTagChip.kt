package com.prography.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.subhead02Bold

@Composable
fun UiTagChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 색상 애니메이션
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Primary else PureWhite,
        animationSpec = tween(durationMillis = 250),
        label = "backgroundColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.Transparent else Gray04,
        animationSpec = tween(durationMillis = 250),
        label = "borderColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) PureWhite else Text01,
        animationSpec = tween(durationMillis = 250),
        label = "textColor"
    )

    val fontWeight by animateIntAsState(
        targetValue = if (isSelected) FontWeight.Bold.weight else FontWeight.Normal.weight,
        animationSpec = tween(250),
        label = "fontWeight"
    )

    Box(
        modifier = modifier
            .border(
                width = if (isSelected) 0.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight(fontWeight),
            style = if (isSelected) subhead02Bold else body02Regular,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}