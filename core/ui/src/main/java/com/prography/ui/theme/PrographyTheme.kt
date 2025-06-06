// core/ui/src/main/java/com/prography/ui/theme/PrographyTheme.kt

package com.prography.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PrographyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Purple80,
            secondary = Pink80,
            background = DarkGray
        )
    } else {
        lightColorScheme(
            primary = Purple40,
            secondary = Pink40,
            background = White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
