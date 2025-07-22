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
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text03
import com.prography.ui.theme.subhead02Bold

enum class ButtonSize {
    LARGE,
    MEDIUM,
    SMALL
}

enum class ButtonType {
    DEFAULT,
    SUB
}

@Composable
fun UiLabelAddButton(
    text: String,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.MEDIUM,
    type: ButtonType = ButtonType.DEFAULT,
    onClick: () -> Unit
) {
    val paddingValues = when (size) {
        ButtonSize.LARGE -> PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ButtonSize.SMALL -> PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    }

    val (backgroundColor, textColor) = when (type) {
        ButtonType.DEFAULT -> Primary to Color.White
        ButtonType.SUB -> Gray02 to Text03
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(4.dp),
        contentPadding = paddingValues,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // 그림자 없음
    ) {
        Text(
            text = text,
            style = subhead02Bold.copy(color = textColor)
        )
    }
}
