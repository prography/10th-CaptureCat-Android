package com.prography.home.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.ButtonType
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.theme.Gray05
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.White
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.caption02Regular
import com.prography.ui.theme.headline02Bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteChoiceBottomSheet(
    onDeleteNow: () -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Black.copy(alpha = 0.40f),
        dragHandle = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(Gray05.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        DeleteChoiceBottomSheetContent(
            onDeleteNow = onDeleteNow,
            onLater = onDismiss
        )
    }
}


@Composable
private fun DeleteChoiceBottomSheetContent(
    onDeleteNow: () -> Unit = {},
    onLater: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .background(White)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.delete_choice_title_prefix))
                withStyle(style = SpanStyle(color = Primary)) {
                    append(stringResource(R.string.delete_choice_title_emphasis))
                }
                append(stringResource(R.string.delete_choice_title_suffix))
            },
            style = headline02Bold,
            color = Text01
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.delete_choice_description),
            style = body02Regular,
            color = Text01,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(26.dp))

        UiLabelAddButton(
            onClick = onDeleteNow,
            text = stringResource(R.string.delete_choice_allow),
            size = ButtonSize.LARGE,
            type = ButtonType.DEFAULT,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        UiLabelAddButton(
            onClick = onLater,
            text = stringResource(R.string.delete_choice_later),
            size = ButtonSize.LARGE,
            type = ButtonType.SUB,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.delete_choice_setting_info),
            style = caption02Regular,
            color = Text03
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteChoiceBottomSheetContent() {
    androidx.compose.material3.Surface {
        DeleteChoiceBottomSheetContent()
    }
}
