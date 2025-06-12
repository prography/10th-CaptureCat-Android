package com.prography.organize.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 뒤로가기 버튼
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
            contentDescription = "뒤로가기",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable { onNavigateUp() }
        )

        // 제목
        Text(
            text = "태그하기 $currentIndex / $totalCount",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.White,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.align(Alignment.Center)
        )

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