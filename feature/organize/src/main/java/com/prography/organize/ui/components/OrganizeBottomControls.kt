package com.prography.organize.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.component.UiTagChip
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.caption02Regular
import com.prography.ui.theme.subhead01Bold

@Composable
fun OrganizeBottomControls(
    availableTags: List<String> = emptyList(),
    selectedTags: List<String> = emptyList(),
    onTagToggle: (String) -> Unit = {},
    onAddTag: () -> Unit = {}
) {
    // 기본 태그들 (예시 데이터) - availableTags가 비어있을 때만 사용
    val defaultAvailableTags = listOf(
        "소원", "쇼핑", "음식"
    )

    val actualAvailableTags = if (availableTags.isEmpty()) defaultAvailableTags else availableTags
    val selectedCount = selectedTags.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 24.dp)
    ) {
        // 제목과 개수 제한 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 추가한 태그",
                style = subhead01Bold,
                color = Text01
            )

            Text(
                text = "태그는 최대 4개까지 지정할 수 있어요",
                style = caption02Regular,
                color = Text03
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(actualAvailableTags) { tagText ->
                UiTagChip(
                    text = tagText,
                    isSelected = selectedTags.contains(tagText),
                    onClick = {
                        // 최대 4개 제한 체크
                        if (!selectedTags.contains(tagText) && selectedCount >= 4) {
                            // 이미 4개가 선택되어 있으면 선택 불가
                            return@UiTagChip
                        }
                        onTagToggle(tagText)
                    }
                )
            }

            item {
                // 태그 추가 버튼
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.5.dp,
                            color = Gray04,
                            shape = RoundedCornerShape(99.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(99.dp))
                        .clickableWithoutRipple { onAddTag() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = com.prography.ui.R.drawable.ic_tag_plus),
                        contentDescription = "태그 추가",
                        tint = Text01
                    )
                }
            }
        }
    }
}

// Preview 함수들
@Preview(showBackground = true)
@Composable
fun OrganizeBottomControlsPreview() {
    OrganizeBottomControls()
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun OrganizeBottomControlsWidePreview() {
    val customAvailableTags = listOf(
        "다이소", "쇼핑", "음식"
    )
    val customSelectedTags = listOf(
        "소원", "여행", "레퍼런스", "다이소"
    )

    OrganizeBottomControls(
        availableTags = customAvailableTags,
        selectedTags = customSelectedTags,
        onTagToggle = { tagText -> println("Toggle tag: $tagText") },
        onAddTag = { println("Add new tag") }
    )
}