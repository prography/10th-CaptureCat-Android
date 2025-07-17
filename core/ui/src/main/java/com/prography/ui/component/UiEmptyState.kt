package com.prography.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.headline02Bold

@Composable
fun UiEmptyState(
    title: String,
    info: String,
    buttonText: String = "임시보관함 가기",
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = headline02Bold,
                color = Text02,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = info,
                style = body01Regular.copy(lineHeight = 25.92.sp),
                color = Text03,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            UiLabelAddButton(
                text = buttonText,
                onClick = onClick
            )
        }
    }
}
