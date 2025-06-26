package com.prography.organize.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.organize.ui.TagChip
import com.prography.ui.theme.Text01
import com.prography.ui.theme.subhead01Bold

data class TagItem(
    val id: String,
    val text: String,
    val isSelected: Boolean,
    val isAddButton: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganizeBottomControls(
    tags: List<TagItem> = emptyList(),
    onTagToggle: (String) -> Unit = {},
    onAddTag: () -> Unit = {}
) {
    // 기본 태그들 (예시 데이터) - 태그 추가 버튼 제외  
    val defaultTags = listOf(
        TagItem("1", "소원", true),
        TagItem("6", "쇼핑", false),
        TagItem("7", "음식", true)
    )

    val actualTags = if (tags.isEmpty()) defaultTags else tags
    // 태그 추가 버튼을 마지막에 추가
    val allTags = actualTags + TagItem("add", "태그 추가 +", false, true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 20.dp, bottom = 26.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "최근 추가한 태그",
            style = subhead01Bold,
            color = Text01,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // FlowRow로 완전히 wrap 크기의 태그들 표시 (최대 2줄)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxLines = 2 // 최대 2줄로 제한
        ) {
            // 태그가 많으면 일부만 표시하고 마지막에 태그 추가 버튼
            val displayTags = if (allTags.size > 12) {
                allTags.take(11) + allTags.last() // 마지막은 태그 추가 버튼
            } else {
                allTags
            }

            displayTags.forEach { tag ->
                TagChip(
                    text = tag.text,
                    isSelected = tag.isSelected,
                    isAddButton = tag.isAddButton,
                    onClick = {
                        if (tag.isAddButton) {
                            onAddTag()
                        } else {
                            onTagToggle(tag.id)
                        }
                    }
                )
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
    val customTags = listOf(
        TagItem("1", "긴 태그 이름", true),
        TagItem("2", "짧은", false),
        TagItem("3", "매우 긴 태그 이름이어서 길게 표시될 것", false),
        TagItem("4", "중간", true),
        TagItem("5", "UI", false),
        TagItem("6", "디자인", true)
    )

    OrganizeBottomControls(
        tags = customTags,
        onTagToggle = { tagId -> println("Toggle tag: $tagId") },
        onAddTag = { println("Add new tag") }
    )
}