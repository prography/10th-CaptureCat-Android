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
import com.prography.ui.R

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
            .background(Color(0xFFFF6F0F))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // 뒤로가기 버튼
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "뒤로가기",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable { onNavigateUp() }
        )

        // 제목
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "태그하기 $currentIndex",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.pretendard_semibold))
                )
            )
            Text(
                text = " / $totalCount",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.pretendard_regular))
                )
            )
        }

        // 완료 버튼
        Text(
            text = "완료",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onComplete() }
        )
    }
}
