package com.prography.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.TagWithCount
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiPrimaryButton
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TagSettingScreen(
    viewModel: TagSettingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToTagAdd: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTags()
    }

    TagSettingContent(
        tags = uiState.tags,
        tagCount = uiState.tagCount,
        isEditMode = uiState.isEditMode,
        isLoading = uiState.isLoading,
        selectedTags = uiState.selectedTags,
        onNavigateBack = onNavigateBack,
        onToggleEditMode = viewModel::toggleEditMode,
        onTagClick = viewModel::toggleTagSelection,
        onDeleteTag = { tag ->
            if (tag == "") viewModel.deleteAllTags()
            else if (tag == "_SELECTED_") viewModel.deleteSelectedTags()
            // else viewModel.deleteTag(tag)
        },
        onNavigateToTagAdd = onNavigateToTagAdd
    )
}

@Composable
private fun TagSettingContent(
    tags: List<TagWithCount>,
    tagCount: Int,
    isEditMode: Boolean,
    isLoading: Boolean,
    selectedTags: Set<String>,
    onNavigateBack: () -> Unit,
    onToggleEditMode: () -> Unit,
    onTagClick: (String) -> Unit,
    onDeleteTag: (String) -> Unit,
    onNavigateToTagAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = com.prography.ui.R.drawable.ic_arrow_backward),
                    contentDescription = stringResource(id = com.prography.ui.R.string.common_back),
                    tint = Text02,
                    modifier = Modifier.clickableWithoutRipple { onNavigateBack() }
                )
                Text(
                    text = "태그 설정",
                    style = headline02Bold,
                    color = Text02
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "($tagCount/30)",
                    style = headline02Regular,
                    color = Text03
                )
            }

            Text(
                text = if (isEditMode) "완료" else "편집",
                style = body01Regular,
                color = Gray02,
                modifier = Modifier
                    .clickable { onToggleEditMode() }
            )
        }

        // Divider
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Divider)
        )

        if (!isEditMode) {
            var text by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            TagInputWithRegister(
                value = text,
                onValueChange = {
                    text = it
                    // TODO: 중복 등 검증/에러 세팅 로직
                },
                errorMessage = errorMessage,
                onClear = { text = "" },
                onRegister = { /* 등록로직 */ },
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
            )
        }

        if (tags.isEmpty()) {
            // Empty state
            EmptyTagState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Tag list
            TagList(
                tags = tags,
                isEditMode = isEditMode,
                selectedTags = selectedTags,
                onTagClick = onTagClick,
                onDeleteTag = onDeleteTag,
                onNavigateToTagAdd = onNavigateToTagAdd,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmptyTagState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "등록된 태그가 없어요.",
            style = headline02Bold,
            color = Text02,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "태그로 분류하면 원하는 이미지를\n" +
                    "쉽게 찾을 수 있어요!",
            style = body01Regular,
            color = Text03,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TagList(
    tags: List<TagWithCount>,
    isEditMode: Boolean,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit,
    onDeleteTag: (String) -> Unit,
    onNavigateToTagAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = if (isEditMode) 80.dp else 0.dp
            )
        ) {
            items(tags) { tagWithCount ->
                TagListItem(
                    tag = tagWithCount,
                    isEditMode = isEditMode,
                    checked = selectedTags.contains(tagWithCount.tag),
                    onCheckToggle = onTagClick,
                    onDeleteTag = onDeleteTag
                )
            }

        }
        if (isEditMode) {
            EditModeBottomBar(
                enabled = selectedTags.isNotEmpty(),
                onDelete = { onDeleteTag("") },
                onDeleteSelected = { onDeleteTag("_SELECTED_") },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TagListItem(
    tag: TagWithCount,
    isEditMode: Boolean,
    checked: Boolean = false,
    onCheckToggle: (String) -> Unit = {},
    onDeleteTag: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                enabled = isEditMode,
                value = checked,
                onValueChange = { onCheckToggle(tag.tag) })
            .padding(16.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditMode) {
            Icon(
                painter = painterResource(id = if (checked) com.prography.ui.R.drawable.ic_check_box_able else com.prography.ui.R.drawable.ic_check_box_unchecked),
                contentDescription = "선택",
                tint = if (checked) Primary else Gray04,
                modifier = Modifier
                    .size(22.dp)
                    .clickableWithoutRipple { onCheckToggle(tag.tag) }
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = tag.tag,
            style = body01Regular,
            color = Text01,
            modifier = Modifier.weight(1f)
        )
        if (!isEditMode) {
            Text(
                text = "${tag.count}회",
                style = caption02Regular,
                color = Text03
            )
        }
    }
}

@Composable
private fun EditModeBottomBar(
    enabled: Boolean,
    onDelete: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Gray02),
            modifier = Modifier.weight(1f)
        ) {
            Text("전체삭제", color = Text01)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onDeleteSelected,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = if (enabled) Primary else Gray02),
            modifier = Modifier.weight(1f)
        ) {
            Text("삭제하기", color = if (enabled) Color.White else Text03)
        }
    }
}

@Composable
fun TagInputWithRegister(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    onClear: () -> Unit = {},
    onRegister: () -> Unit = {},
    placeholder: String = stringResource(com.prography.ui.R.string.image_detail_tag_input_placeholder),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isError = errorMessage != null

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .background(
                    color = Gray01,
                    shape = RoundedCornerShape(6.dp)
                )
                .border(
                    1.dp,
                    color = if (isError) Error else Gray03,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(38.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    enabled = enabled,
                    textStyle = body02Regular.copy(color = Text02),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = if (enabled) Text03 else Gray03,
                                    style = body02Regular,
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = com.prography.ui.R.drawable.ic_text_field_delete),
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
                Button(
                    enabled = value.isNotBlank() && !isError,
                    onClick = onRegister,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("등록", style = body02Regular)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        errorMessage?.let {
            Text(
                text = it,
                color = Error,
                style = caption02Regular
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun TagSettingContentPreview() {
    TagSettingContent(
        tags = listOf(
            TagWithCount("일상", 15),
            TagWithCount("추가된 태그", 8),
            TagWithCount("추가된 태그", 5),
            TagWithCount("추가된 태그", 3),
            TagWithCount("추가된 태그", 2)
        ),
        tagCount = 5,
        isEditMode = false,
        isLoading = false,
        selectedTags = setOf(),
        onNavigateBack = {},
        onToggleEditMode = {},
        onTagClick = {},
        onDeleteTag = {},
        onNavigateToTagAdd = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TagSettingEditModePreview() {
    TagSettingContent(
        tags = listOf(
            TagWithCount("일상", 15),
            TagWithCount("추가된 태그", 8),
            TagWithCount("추가된 태그", 5),
            TagWithCount("추가된 태그", 3)
        ),
        tagCount = 4,
        isEditMode = true,
        isLoading = false,
        selectedTags = setOf(),
        onNavigateBack = {},
        onToggleEditMode = {},
        onTagClick = {},
        onDeleteTag = {},
        onNavigateToTagAdd = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TagSettingEmptyPreview() {
    TagSettingContent(
        tags = emptyList(),
        tagCount = 0,
        isEditMode = false,
        isLoading = false,
        selectedTags = setOf(),
        onNavigateBack = {},
        onToggleEditMode = {},
        onTagClick = {},
        onDeleteTag = {},
        onNavigateToTagAdd = {}
    )
}