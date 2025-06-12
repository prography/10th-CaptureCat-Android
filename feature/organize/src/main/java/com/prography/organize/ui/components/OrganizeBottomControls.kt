package com.prography.organize.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.organize.model.OrganizeScreenshotItem

@Composable
fun OrganizeBottomControls(
    screenshot: OrganizeScreenshotItem?,
    onFavoriteToggle: (Boolean) -> Unit
) {
    screenshot?.let { item ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 즐겨찾기 버튼
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (item.isFavorite) Color.Black else Color.White,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            onFavoriteToggle(!item.isFavorite)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.btn_star_big_on),
                        contentDescription = "즐겨찾기",
                        tint = if (item.isFavorite) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "즐겨찾기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // 미분류 텍스트
                Text(
                    text = "미분류",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // 태그 추가 버튼 (현재는 비활성)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { /* 태그 추가 기능 - 추후 구현 */ }
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_input_add),
                        contentDescription = "태그 추가",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "태그 추가",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Default,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}