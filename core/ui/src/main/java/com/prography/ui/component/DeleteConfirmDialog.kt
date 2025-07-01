package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.prography.ui.R
import com.prography.ui.theme.Gray02
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.subhead02Bold

@Composable
fun DeleteConfirmDialog(
    isVisible: Boolean,
    selectedCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    UiCommonDialog(
        isVisible = isVisible,
        title = "삭제할까요?",
        message = "${selectedCount}개의 항목을 삭제하시겠습니까?\n삭제된 항목은 복구할 수 없습니다.",
        leftButtonText = "취소",
        rightButtonText = "삭제",
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

@Preview
@Composable
fun DeleteConfirmDialogPreview() {
    DeleteConfirmDialog(
        isVisible = true,
        selectedCount = 3,
        onDismiss = {},
        onConfirm = {}
    )
}
