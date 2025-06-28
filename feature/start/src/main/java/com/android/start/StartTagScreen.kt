package com.android.start

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prography.navigation.NavigationHelper
import com.prography.ui.component.ButtonState
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.theme.Gray04
import com.prography.ui.theme.Gray06
import com.prography.ui.theme.Primary
import com.prography.ui.theme.PrimaryPress
import com.prography.ui.theme.PureWhite
import com.prography.ui.theme.body02Regular
import com.prography.ui.theme.headline02Bold
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text03
import com.prography.ui.theme.subhead02Bold

@Composable
fun StartTagScreen(
    tagOptions: List<String> = listOf(
        "쇼핑", "직무 관련", "레퍼런스", "코디",
        "공부", "글귀", "여행", "자기계발",
        "맛집", "노래", "레시피", "운동"
    ),
    maxSelectableTags: Int = 5, // 최대 선택 가능한 태그 개수
    onFinishSelection: (List<String>) -> Unit
) {
    var selectedTags by remember { mutableStateOf(listOf<String>()) }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            // 📍 상단 텍스트 영역
            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "자주 캡처하는 이미지가 있으신가요?",
                    style = headline02Bold,
                    color = Text01
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "관심 주제를 선택(5개 이하)해주시면\n캡처 시 미리 태그로 만들어드려요.",
                    style = body02Regular,
                    color = Text03
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tagOptions.forEach { tag ->
                    val isSelected = tag in selectedTags
                    TagChip(
                        text = tag,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedTags = selectedTags - tag
                            } else if (selectedTags.size < maxSelectableTags) {
                                selectedTags = selectedTags + tag
                            }
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 26.dp)
        ) {
            val buttonState = remember(selectedTags) {
                if (selectedTags.isEmpty()) ButtonState.Disabled
                else ButtonState.Enabled
            }

            // 수정된 부분: UiPrimaryButton 사용
            UiPrimaryButton(
                text = "선택 완료 (${selectedTags.size}/$maxSelectableTags)", // 선택된 개수 렌더링
                onClick = { onFinishSelection(selectedTags) },
                state = buttonState, // 선택 여부에 따라 버튼 활성화 상태 변경
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TagChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 색상 애니메이션
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Primary else PureWhite,
        animationSpec = tween(durationMillis = 250)
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.Transparent else Gray04,
        animationSpec = tween(durationMillis = 250)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) PureWhite else Text01,
        animationSpec = tween(durationMillis = 250)
    )

    val fontWeight by animateIntAsState(
        targetValue = if (isSelected) FontWeight.Bold.weight else FontWeight.Normal.weight,
        animationSpec = tween(250)
    )

    Box(
        modifier = Modifier
            .border(
                width = if (isSelected) 0.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight(fontWeight),
            style = if (isSelected) subhead02Bold else body02Regular,
            color = textColor
        )
    }
}


@Preview(showBackground = true)
@Composable
fun StartTagScreenPreview() {
    StartTagScreen(
        tagOptions = listOf(
            "쇼핑", "직무 관련", "레퍼런스", "코디",
            "공부", "글귀", "여행", "자기계발",
            "맛집", "노래", "레시피", "운동"
        ),
        maxSelectableTags = 5
    ){

    }
}
