package com.prography.home.ui.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.home.R
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.PrographyTheme
import com.prography.ui.theme.myFontFamily
import timber.log.Timber


@Composable
fun HomeScreen(
    onNavigateToStorage: () -> Unit = {}
) {
    val hasScreenshots = false // 예시

    if (!hasScreenshots) {
        HomeEmptyScreen(
            onSaveScreenshotClick = { onNavigateToStorage() }
        )
    } else {
        // TODO: 데이터 있을 때 화면
    }
}

@Composable
fun HomeEmptyScreen(
    onSaveScreenshotClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = com.prography.ui.R.drawable.ic_settings),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 21.dp)
                )
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F6)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "캡처캣은 처음이시죠? 생산성있게 활용하는 방법을 확인해 보세요!",
                        style = typography.displaySmall
                    )
                    Row(
                        modifier = Modifier.padding(0.dp,8.dp,0.dp,0.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "사용방법 보기",
                            color = Color.Black,
                            style = typography.labelSmall,
                            modifier = Modifier.padding(0.dp,0.dp,4.dp,0.dp),
                        )
                        Icon(
                            painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_forward),
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                FilterChip(text = "전체", isSelected = true)
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(text = "미분류", isSelected = false)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // ✅ 여기!
        ) {
            Text(
                text = "저장된 스크린샷이 없어요\n스크린샷을 저장하고 관리해보세요!",
                style = typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            UiPrimaryButton(
                text = "스크린샷 저장하기",
                fontSize = 16.sp,
                onClick = onSaveScreenshotClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color.Black else Color(0xFFF3F3F3),
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .height(36.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF393A40),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeEmptyScreenPreview() {
    PrographyTheme {
        HomeEmptyScreen(
            onSaveScreenshotClick = {} // 미리보기용 빈 콜백
        )
    }
}
