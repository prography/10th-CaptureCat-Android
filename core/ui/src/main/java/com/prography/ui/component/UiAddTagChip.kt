package com.prography.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.*

enum class AddTagChipState {
    FIRST,   // count == 0 → "추가하기" + 아이콘
    NORMAL   // count > 0 → 아이콘만
}

@Composable
fun UiAddTagChip(
    state: AddTagChipState = AddTagChipState.NORMAL,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = Gray02,
        border = BorderStroke(1.dp, Divider),
        modifier = modifier.heightIn(min = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .clickableWithoutRipple(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 5.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state == AddTagChipState.FIRST) {
                Text(
                    text = stringResource(R.string.label_add),
                    style = body02Regular,
                    color = Text01
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Image(
                painter = painterResource(R.drawable.ic_add_chip),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
