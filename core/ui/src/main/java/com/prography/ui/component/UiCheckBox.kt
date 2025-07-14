package com.prography.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.Text02
import com.prography.ui.theme.body02Regular

@Composable
fun UiCheckBox(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable {
            onCheckedChange(!isChecked)
        }
    ) {
        Image(
            painter = painterResource(
                id = if (isChecked)
                    R.drawable.ic_check_box_able
                else
                    R.drawable.ic_check_box_disable
            ),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Text(
            text = text,
            style = body02Regular,
            color = Text02
        )
    }
}