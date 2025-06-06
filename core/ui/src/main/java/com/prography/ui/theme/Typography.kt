// core/ui/src/main/java/com/prography/ui/theme/Typography.kt

package com.prography.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.prography.ui.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular),
    Font(R.font.pretendard_medium),
    Font(R.font.pretendard_semibold),
    Font(R.font.pretendard_bold)
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Pretendard,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        fontSize = 22.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Pretendard,
        fontSize = 12.sp
    )
)