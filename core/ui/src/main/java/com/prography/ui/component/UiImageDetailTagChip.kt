package com.prography.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.R
import com.prography.ui.theme.*

enum class TagChipState { ENABLED, DISABLED, ADD }

@Composable
fun UiImageDetailTagChip(
    text: String,
    state: TagChipState = TagChipState.ENABLED,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = when (state) {
            TagChipState.ENABLED -> Text01
            TagChipState.DISABLED -> Gray04
            TagChipState.ADD -> Text01
        },
        animationSpec = tween(durationMillis = 250),
        label = "textColor"
    )

    val iconRes = when (state) {
        TagChipState.DISABLED -> R.drawable.ic_user_tag_check
        TagChipState.ADD -> R.drawable.ic_add_chip
        else -> null
    }

    Box(
        modifier = modifier
            .heightIn(min = 32.dp)
            .border(1.dp, Divider, RoundedCornerShape(20.dp))
            .background(PureWhite, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 5.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = body02Regular,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            iconRes?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
