package com.prography.setting.ui.notice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.R
import com.prography.ui.component.UiHeader
import com.prography.ui.theme.*

data class NoticeUiModel(
    val title: String,
    val date: String, // "MM/dd"
    val body: String
)

@Composable
fun NoticeScreen(
    navigationHelper: NavigationHelper
) {
    val notices = remember {
        listOf(
            NoticeUiModel(
                title = "캡처캣 서비스 종료 안내",
                date = "01/28",
                body = """
                    안녕하세요. 캡처캣입니다.
                    캡처캣 서비스를 이용해주신 모든 분들께 진심으로 감사드립니다.

                    캡처캣 서비스는 2026년 1월 28일부로 종료될 예정입니다.
                    서비스 종료와 함께 앱 제공이 중단되며, 앱 내에서 확인하던 이미지 및 관련 데이터는 더 이상 접근할 수 없게 됩니다.

                    서비스 종료 이후에도 기존에 확인하던 이미지들을 다시 볼 수 없으므로, 기억해두고 싶은 정보가 있다면 종료 전 미리 캡처해 두시길 부탁드립니다.

                    그동안 캡처캣 서비스를 이용해 주신 분들께
                    다시 한번 감사의 인사를 드립니다.
                """.trimIndent()
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        UiHeader(
            title = "공지사항",
            showBackButton = true
        ) {
            navigationHelper.navigate(NavigationEvent.Up)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            notices.forEachIndexed { index, notice ->
                NoticeAccordionItem(
                    notice = notice,
                    modifier = Modifier.fillMaxWidth()
                )

                // 아이템 구분선
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Divider)
                )
            }
        }
    }
}

@Composable
private fun NoticeAccordionItem(
    notice: NoticeUiModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                expanded = !expanded
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray01)
                .padding(horizontal = 16.dp, vertical = 9.5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notice.title,
                    style = body02Regular,
                    color = Text01,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = notice.date,
                    style = caption02Regular,
                    color = Text03
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_icon),
                contentDescription = null,
                tint = Text03,
                modifier = Modifier.rotate(if (expanded) 180f else 0f)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = notice.body,
                    style = body02Regular,
                    color = Text01,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
