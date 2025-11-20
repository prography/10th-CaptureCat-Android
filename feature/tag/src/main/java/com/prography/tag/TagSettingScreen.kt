package com.prography.tag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.domain.model.TagWithCount
import com.prography.ui.R
import com.prography.ui.component.BottomInputButtonVariant
import com.prography.ui.component.ButtonSize
import com.prography.ui.component.TagAddBottomSheet
import com.prography.ui.component.TagEditBottomSheet
import com.prography.ui.component.UiBottomInputButton
import com.prography.ui.component.UiLabelAddButton
import com.prography.ui.component.clickableWithoutRipple
import com.prography.ui.theme.*

@Composable
fun TagSettingScreen(
) {

    val viewModel: TagSettingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var editTarget by remember { mutableStateOf<TagWithCount?>(null) }
    var showEditSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTags()
    }

    val tagsWithCount = uiState.tags.map { tagModel ->
        TagWithCount(
            id = tagModel.id?.toInt() ?: 0,
            tag = tagModel.name,
            count = 0 // 로컬에서는 카운트 정보가 없음
        )
    }

    TagSettingContent(
        tags = tagsWithCount,
        tagCount = uiState.tagCount,
        isEditMode = uiState.isEditMode,
        isLoading = uiState.isLoading,
        selectedTags = uiState.selectedTags,
        errorMessage = uiState.errorMessage,
        onNavigateBack = { viewModel.handleAction(TagSettingAction.NavigateBack)},
        onToggleEditMode = { viewModel.handleAction(TagSettingAction.ToggleEditMode) },
        onTagClick = { tag -> viewModel.handleAction(TagSettingAction.ToggleTagSelection(tag)) },
        onDeleteTag = { tag ->
            when (tag) {
                "_SELECTED_" -> viewModel.handleAction(TagSettingAction.DeleteSelectedTags)
                "_ALL_" -> viewModel.handleAction(TagSettingAction.SelectAllTags)
            }
        },
        onTagAdd = { inputTag -> viewModel.handleAction(TagSettingAction.AddInputTag(inputTag)) },
        onOpenEdit = { tag ->
            editTarget = tag
            showEditSheet = true
        }
    )
    if (!uiState.isLoggedIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickableWithoutRipple(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            UiLabelAddButton(
                text = stringResource(R.string.storage_login_required),
                size = ButtonSize.LARGE,
                onClick = { viewModel.handleAction(TagSettingAction.NavigateToLogin) },
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
    // ✅ 수정 시트
    if (showEditSheet && editTarget != null) {
        val tgt = editTarget!!
        // id가 0이면(로컬/임시) 서버 업데이트 불가 → 버튼 비활성화 or 안내
        val tagId = tgt.id?.toLong() ?: 0L

        TagEditBottomSheet(
            tagId = tagId,
            initialText = tgt.tag,
            onSubmit = { id, newName ->
                viewModel.handleAction(TagSettingAction.UpdateTag(id, newName))
                showEditSheet = false
            },
            onDismiss = { showEditSheet = false }
        )
    }
}

@Composable
private fun TagSettingContent(
    tags: List<TagWithCount>,
    tagCount: Int,
    isEditMode: Boolean,
    isLoading: Boolean,
    selectedTags: Set<String>,
    errorMessage : String?,
    onNavigateBack: () -> Unit,
    onToggleEditMode: () -> Unit,
    onTagClick: (String) -> Unit,
    onDeleteTag: (String) -> Unit,
    onTagAdd: (String) -> Unit,
    onOpenEdit: (TagWithCount) -> Unit
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
                    text = stringResource(com.prography.ui.R.string.setting_tag_settings),
                    style = headline02Bold,
                    color = Text02
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "($tagCount/40)",
                    style = headline02Regular,
                    color = Text03
                )
            }

            Text(
                text = if (isEditMode) stringResource(com.prography.ui.R.string.common_complete) else stringResource(com.prography.ui.R.string.label_edit_mode),
                style = body01Regular,
                color = Text03,
                modifier = Modifier
                    .clickableWithoutRipple { onToggleEditMode() }
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
            TagInputWithRegister(
                value = text,
                onValueChange = {
                    text = it
                },
                errorMessage = errorMessage,
                onClear = { text = "" },
                onRegister = { onTagAdd(text) },
                modifier = Modifier
                    .padding(16.dp)
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
                onNavigateToTagAdd = onTagAdd,
                modifier = Modifier.weight(1f),
                onEdit = { tag -> onOpenEdit(tag) }
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
            text = stringResource(com.prography.ui.R.string.label_no_tags_yet),
            style = headline02Bold,
            color = Text03,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(com.prography.ui.R.string.label_tag_description),
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
    onEdit: (TagWithCount) -> Unit,
    onNavigateToTagAdd: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listBottomPadding by animateDpAsState(
        targetValue = if (isEditMode) 96.dp else 0.dp, // 바텀바 높이 + 여유
        animationSpec = tween(220),
        label = "listBottomPadding"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = listBottomPadding
            )
        ) {
            items(tags) { tagWithCount ->
                TagListItem(
                    tag = tagWithCount,
                    isEditMode = isEditMode,
                    checked = selectedTags.contains(tagWithCount.tag),
                    onCheckToggle = onTagClick,
                    onDeleteTag = onDeleteTag,
                    onEdit = onEdit
                )
            }
        }

        AnimatedVisibility(
            visible = isEditMode,
            enter = slideInVertically(
                initialOffsetY = { it } // 아래에서 위로
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it } // 위에서 아래로
            ) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            EditModeBottomBar(
                enabled = selectedTags.isNotEmpty(),
                onDelete = { onDeleteTag("_ALL_") },
                onDeleteSelected = { onDeleteTag("_SELECTED_") },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // 시스템 바 피하기
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
    onDeleteTag: (String) -> Unit = {},
    onEdit: (TagWithCount) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                enabled = isEditMode,
                value = checked,
                onValueChange = { onCheckToggle(tag.tag) })
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditMode) {
            Icon(
                painter = painterResource(
                    id = if (checked) R.drawable.ic_check_box_able
                    else R.drawable.ic_check_box_disable
                ),
                contentDescription = stringResource(com.prography.ui.R.string.cd_select),
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = tag.tag,
            style = body01Regular.copy(color = Text01),
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )

        if (!isEditMode) {
            Text(
                text = stringResource(com.prography.ui.R.string.label_edit),
                style = body01Regular,
                color = Gray05,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickableWithoutRipple { onEdit(tag) }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Divider)
    )
}

@Composable
private fun EditModeBottomBar(
    enabled: Boolean,
    onDelete: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 상단 구분선
        HorizontalDivider(
            color = Divider,
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UiBottomInputButton(
                onClick = onDelete,
                text = stringResource(com.prography.ui.R.string.label_delete_all),
                enabled = true,
                modifier = Modifier.weight(1f),
                variant = BottomInputButtonVariant.Sub
            )
            Spacer(Modifier.width(10.dp))
            UiBottomInputButton(
                onClick = onDeleteSelected,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                text = stringResource(com.prography.ui.R.string.label_delete_button)
            )
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
    placeholder: String = stringResource(R.string.image_detail_tag_input_placeholder),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
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
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    enabled = enabled,
                    textStyle = body02Regular.copy(color = Text02),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 26.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = if (enabled) Text03 else Gray06,
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(force = true)
                            keyboard?.hide()
                            onRegister()
                        }
                    ),
                )
                if (value.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = com.prography.ui.R.drawable.ic_text_field_delete),
                        contentDescription = "Clear",
                        tint = Secondary,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(20.dp)
                            .clickableWithoutRipple { onClear() }
                    )
                }
                Text(
                    text = "등록",
                    color = if (isError) Gray04 else Text03,
                    style = body02Regular,
                    modifier = Modifier
                        .clickableWithoutRipple(enabled = value.isNotBlank() && !isError) {
                            focusManager.clearFocus(force = true)
                            keyboard?.hide()
                            onRegister()
                        }
                )
            }
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                color = Error,
                style = caption02Regular
            )
        }
    }
}
