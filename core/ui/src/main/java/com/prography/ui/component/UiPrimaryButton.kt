package com.prography.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size // Ensure Modifier.size is properly imported
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.R
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Gray06
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PrimaryPress
import kotlin.math.roundToInt

enum class ButtonState {
    Enabled,
    Pressed,
    Disabled,
    Loading
}

@Composable
fun UiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    state: ButtonState = ButtonState.Enabled
) {
    val colors = when (state) {
        ButtonState.Enabled -> ButtonDefaults.buttonColors(containerColor = Primary)
        ButtonState.Pressed -> ButtonDefaults.buttonColors(containerColor = PrimaryPress)
        ButtonState.Disabled -> ButtonDefaults.buttonColors(containerColor = Gray04)
        ButtonState.Loading -> ButtonDefaults.buttonColors(containerColor = Primary)
    }

    Button(
        onClick = { if (state == ButtonState.Enabled) onClick() },
        modifier = modifier.height(55.dp),
        shape = RoundedCornerShape(6.dp),
        colors = colors,
        enabled = state != ButtonState.Disabled && state != ButtonState.Loading
    ) {
        if (state == ButtonState.Loading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = text,
                color = if (state == ButtonState.Disabled) Gray06 else Color.White,
                fontSize = fontSize,
                fontFamily = FontFamily(Font(R.font.prography_pretendard_semibold))
            )
        }
    }
}
