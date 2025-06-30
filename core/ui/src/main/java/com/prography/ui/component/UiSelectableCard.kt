package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Gray01
import com.prography.ui.theme.Gray07
import com.prography.ui.theme.Text01
import com.prography.ui.theme.body01Regular

@Composable
fun SelectableCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Gray07 else Color.Transparent
    val backgroundColor = Gray01

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .border(
                width = if (selected) 1.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = body01Regular,
            color = Text01
        )
        RadioButton(
            selected = selected,
            onClick = null, // 전체 Row 클릭으로 처리
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Black,
                unselectedColor = Color(0xFFDADADA)
            )
        )
    }
}

@Preview(name = "선택됨")
@Composable
fun Preview_SelectedSelectableCard() {
    SelectableCard(
        text = "캡쳐캣을 사용하기 어려움.",
        selected = true,
        onClick = {}
    )
}

@Preview(name = "선택되지 않음")
@Composable
fun Preview_UnselectedSelectableCard() {
    SelectableCard(
        text = "캡쳐캣을 사용하기 어려움.",
        selected = false,
        onClick = {}
    )
}
