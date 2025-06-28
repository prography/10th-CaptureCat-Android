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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.home.ui.storage.contract.ScreenshotAction
import com.prography.ui.theme.PrographyTheme
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.Text03
import com.prography.ui.theme.body01Regular
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold


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
                .background(Color.White)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 8.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.tab_storage),
                        style = headline02Bold,
                        color = Text01
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.organize_count_message, totalCount),
                        style = body02Regular,
                        color = Text01
                    )
                }
                Text(
                    modifier = Modifier
                        .clickable {
                            onAction(ScreenshotAction.OrganizeSelected)
                        }
                        .padding(14.dp, 8.dp),
                    text = "다음",
                    color = Text01,
                    style = body01Regular
                )
            }
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
                    val action =
                        if (isAllSelected) ScreenshotAction.CancelSelection else ScreenshotAction.SelectAll
                    onAction(action)
                }
            ) {
                Image(
                    painter = painterResource(
                        id = if (isAllSelected)
                            R.drawable.ic_check_box_able
                        else
                            R.drawable.ic_check_box_disable
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(
                    text = "전체 선택",
                    style = body02Regular,
                    color = Text02
                )
            }
            Text(
                text = "선택 삭제",
                style = body02Regular,
                color = Text03,
                modifier = Modifier.clickable {
                    onAction(ScreenshotAction.DeleteSelected)
                }
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
        isAllSelected = true,
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
