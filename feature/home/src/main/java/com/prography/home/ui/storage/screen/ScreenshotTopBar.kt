package com.prography.home.ui.storage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.home.R
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.ui.theme.PrographyTheme


@Composable
fun ScreenshotTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    totalCount: Int,
    isAllSelected: Boolean,
    onAction: (ScreenshotAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF6F0F))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = if (isSelectionMode) "선택 $selectedCount" else "${totalCount}건의 스크린샷을\n정리해보세요",
                fontSize = 19.sp,
                lineHeight = 26.sp,
                fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_semibold)),
                color = Color.White
            )
        }

        // 체크박스/선택취소 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onAction(
                        if (isSelectionMode) ScreenshotAction.CancelSelection
                        else ScreenshotAction.SelectAll
                    )
                }
            ) {
                Image(
                    painter = painterResource(
                        id = if (isAllSelected)
                            com.prography.ui.R.drawable.ic_check_box
                        else
                            com.prography.ui.R.drawable.ic_check_box_outline_blank
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "전체 선택",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_semibold)),
                        color = Color(0xFF222222)
                    )
                )
            }

            if (isSelectionMode) {
                Text(
                    text = "선택 취소",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_regular)),
                        color = Color.Gray
                    ),
                    modifier = Modifier.clickable {
                        onAction(ScreenshotAction.CancelSelection)
                    }
                )
            }
        }
    }
}

@Composable
fun ScreenshotBottomBar(
    selectedCount: Int,
    onAction: (ScreenshotAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .background(color = Color(0xFFFFE2C8), shape = RoundedCornerShape(6.dp))
                .clickable { onAction(ScreenshotAction.DeleteSelected) }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "삭제하기",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_semibold)),
                    color = Color(0xFFFF6F0F)
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .background(color = Color(0xFFFF6F0F), shape = RoundedCornerShape(6.dp))
                .clickable { onAction(ScreenshotAction.OrganizeSelected) }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${selectedCount}장 정리하기",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_semibold)),
                    color = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "TopBar - 선택 전")
@Composable
fun ScreenshotTopBarPreview_Default() {
    ScreenshotTopBar(
        isSelectionMode = false,
        selectedCount = 0,
        totalCount = 12,
        isAllSelected = false,
        onAction = {}
    )
}

@Preview(showBackground = true, name = "TopBar - 선택 중")
@Composable
fun ScreenshotTopBarPreview_Selected() {
    ScreenshotTopBar(
        isSelectionMode = true,
        selectedCount = 3,
        totalCount = 12,
        isAllSelected = false,
        onAction = {}
    )
}

@Preview(showBackground = true, name = "BottomBar - 선택 중")
@Composable
fun ScreenshotBottomBarPreview() {
    PrographyTheme {
        ScreenshotBottomBar(
            selectedCount = 3,
            onAction = {}
        )
    }
}