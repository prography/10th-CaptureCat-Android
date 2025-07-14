package com.prography.organize.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview

import com.prography.ui.R
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.UiButtonText
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.theme.Primary
import com.prography.ui.theme.Text01
import com.prography.ui.theme.headline03Bold
import com.prography.ui.theme.headline03Regular
import com.prography.ui.theme.subhead01Bold

@Composable
fun OrganizeTopBar(
    currentIndex: Int,
    totalCount: Int,
    onNavigateUp: () -> Unit,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
    ) {


        // 제목
        Row(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            // 뒤로가기 버튼
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_backward),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNavigateUp() }
            )
            Text(
                text = if (currentIndex == 0) "태그하기 ${totalCount}" else "태그하기 $currentIndex/$totalCount",
                style = headline03Bold,
                color = Text01
            )
        }

        // 완료 버튼
        UiButtonText(
            text = "저장",
            onClick = { onComplete()},
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun OrganizeTopBarBatchModePreview() {
    OrganizeTopBar(
        currentIndex = 0, // 한번에 모드일 때는 0
        totalCount = 15,
        onNavigateUp = { },
        onComplete = { }
    )
}

@Preview(showBackground = true)
@Composable
fun OrganizeTopBarSingleModePreview() {
    OrganizeTopBar(
        currentIndex = 3, // 한장씩 모드일 때는 현재 페이지
        totalCount = 8,
        onNavigateUp = { },
        onComplete = { }
    )
}
