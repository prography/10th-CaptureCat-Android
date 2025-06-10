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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // 상단 안내 카드
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "캡처캣은 처음이시죠? 생산성있게 활용하는 방법을 확인해 보세요!",
                        style = typography.bodyLarge
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        TextButton(onClick = { /* TODO: 사용법 보기 클릭 처리 */ }) {
                            Text(text = "사용법 보기",
                                color = Color.Black,
                                style = typography.bodyMedium)
                        }
                        Icon(
                            painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_forward),
                            contentDescription = ""
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 필터 탭 (전체 / 미분류)
            Row {
                FilterChip(text = "전체", isSelected = true)
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(text = "미분류", isSelected = false)
            }

            Spacer(modifier = Modifier.height(80.dp))

            // "스크린샷이 없어요" 안내 텍스트
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장된 스크린샷이 없어요", style = typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("스크린샷을 저장하고 관리해보세요!", style = typography.bodyMedium)
            }
        }

        // 하단 버튼
        UiPrimaryButton(
            text = "스크린샷 저장하기",
            onClick = onSaveScreenshotClick,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color.Black else Color(0xFFF3F3F3),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .height(36.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
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