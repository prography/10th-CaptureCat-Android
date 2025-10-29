import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prography.imageDetail.ui.content.Sheet
import com.prography.imageDetail.ui.contract.ImageDetailAction
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.ui.component.TagChipState
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiImageDetailTagChip
import com.prography.ui.component.UiTagSelectedChip
import com.prography.ui.component.UnderlinedClickableText
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.Gray05
import com.prography.ui.theme.Text01
import com.prography.ui.theme.Text02
import com.prography.ui.theme.headline03Bold
import com.prography.ui.theme.subhead01Bold


@Composable
fun TagEditContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeVisible by remember { derivedStateOf { ime.getBottom(density) > 0 } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_close),
                contentDescription = stringResource(com.prography.ui.R.string.common_close),
                tint = Text01,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickableWithoutRipple { onAction(ImageDetailAction.HideSheet) }
            )
            Text(
                text = "태그 수정",
                style = headline03Bold,
                color = Text01,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Registered tags
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "등록된 태그", style = subhead01Bold, color = Text02)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${state.currentScreenshot?.tags?.size ?: 0}/4)",
                    style = subhead01Bold,
                    color = Text02
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp * 2 + 8.dp)
            ) {
                state.currentScreenshot?.tags?.forEach { tag ->
                    UiTagSelectedChip(
                        text = tag.name,
                        onDelete = { onAction(ImageDetailAction.OnTagDelete(tag)) }
                    )
                }
                UiImageDetailTagChip(
                    text = "추가하기",
                    state = TagChipState.ADD,
                    onClick = { onAction(ImageDetailAction.ShowSheet(Sheet.Add)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User tags pool
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "기존태그 보기", style = subhead01Bold, color = Text02)
                Row {
                    UnderlinedClickableText(
                        text = if (state.isUserTagsExpanded) "접기" else "더보기",
                        onClick = { onAction(ImageDetailAction.OnToggleUserTagsExpanded) }
                    )
                    Icon(
                        painter = painterResource(id = com.prography.ui.R.drawable.ic_keyboard_arrow_down),
                        contentDescription = stringResource(com.prography.ui.R.string.common_search),
                        tint = Gray05,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val registeredIds = state.currentScreenshot?.tags?.map { it.id }?.toSet() ?: emptySet()
            val pool = if (state.isUserTagsExpanded) state.userTags else state.userTags.take(8)

            if (state.currentScreenshot != null) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    pool.forEach { tag ->
                        val disabled = tag.id in registeredIds
                        UiImageDetailTagChip(
                            text = tag.name,
                            state = if (disabled) TagChipState.DISABLED else TagChipState.ENABLED,
                            onClick = { onAction(ImageDetailAction.OnClickUserTag(tag)) }
                        )
                    }
                }
            } else {
                Text("아직 등록된 태그가 없어요")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imeVisible) {
            UiBottomInputButton(
                text = stringResource(com.prography.ui.R.string.common_complete),
                enabled = state.newTagText.isNotBlank(),
                onClick = {
                    if (state.newTagText.isNotBlank()) {
                        onAction(ImageDetailAction.OnAddNewTag)
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}