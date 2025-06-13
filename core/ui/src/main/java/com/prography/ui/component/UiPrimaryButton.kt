package com.prography.ui.component

import androidx.compose.foundation.layout.height
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

@Composable
fun UiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(55.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F0F))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontFamily = FontFamily(Font(R.font.prography_pretendard_semibold))
        )
    }
}
