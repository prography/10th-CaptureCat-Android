package com.prography.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = this.clickable(
    enabled = enabled,
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick
)
