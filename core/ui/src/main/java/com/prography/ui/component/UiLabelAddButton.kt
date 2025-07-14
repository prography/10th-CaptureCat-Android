package com.prography.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Primary
import com.prography.ui.theme.subhead02Bold

enum class ButtonSize {
    LARGE,
    MEDIUM,
    SMALL
}

@Composable
fun UiLabelAddButton(
    text: String,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.MEDIUM,
    onClick: () -> Unit
) {
    val paddingValues = when (size) {
        ButtonSize.LARGE -> PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ButtonSize.SMALL -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        shape = RoundedCornerShape(4.dp),
        contentPadding = paddingValues,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // 그림자 없음
    ) {
        Text(
            text = text,
            style = subhead02Bold.copy(color = Color.White)
        )
    }
}
