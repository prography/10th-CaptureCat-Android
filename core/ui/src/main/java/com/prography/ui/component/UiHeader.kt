package com.prography.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.Divider
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.headline02Bold

/**
 * 공통 헤더 UI 컴포넌트
 *
 * @param title 헤더 제목
 * @param showBackButton 뒤로가기 버튼 표시 여부
 * @param onBackClick 뒤로가기 버튼 클릭 시 호출될 콜백
 */
@Composable
fun UiHeader(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            if (showBackButton) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_backward),
                    contentDescription = stringResource(id = R.string.common_back),
                    modifier = Modifier
                        .clickableWithoutRipple { onBackClick() },
                    tint = Text02
                )
            }

            Text(
                text = title,
                style = headline02Bold,
                color = Text01
            )
        }
        // 👇 아래쪽 1dp 선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Divider)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UiHeaderPreview() {
    UiHeader(title = "마이", showBackButton = true)
}

@Preview(showBackground = true)
@Composable
fun UiHeaderNoBackPreview() {
    UiHeader(title = "마이")
}
