package com.prography.home.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.ui.theme.*

@Composable
fun ErrorReportBanner(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = Color(0x24FF6600)
            )
            .clickable { onReportClick() }
            .padding(start = 24.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "채팅으로 오류 제보하기",
                        style = subhead01Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "보내주신 내용은 모두 확인하고 답변드려요",
                        style = caption02Regular,
                        color = Text02
                    )
                }
            }

            Image(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_report),
                contentDescription = "닫기",
                modifier = Modifier.size(76.dp, 70.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorReportBannerPreview() {
    PrographyTheme {
        ErrorReportBanner(
            onReportClick = {},
        )
    }
}