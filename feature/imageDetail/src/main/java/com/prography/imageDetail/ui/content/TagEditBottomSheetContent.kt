/*
package com.prography.imageDetail.ui.content

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TagEditBottomSheetContent(
    state: ImageDetailState,
    onAction: (ImageDetailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isMaxTagsReached = (state.currentScreenshot?.tags?.size ?: 0) >= 4

    // 키보드 상태 확인
    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeVisible by remember {
        derivedStateOf { ime.getBottom(density) > 0 }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp) // 완료 버튼 여백
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(R.string.common_close),
                tint = Text01,
                modifier = Modifier
                    .size(24.dp)
                    .clickableWithoutRipple { onAction(ImageDetailAction.OnHideTagEditBottomSheet) }
            )
            Text(
                text = stringResource(R.string.image_detail_tag_add),
                style = headline03Bold,
                color = Text01
            )
        }

        // 입력창
        TagInputField(
            value = state.newTagText,
            onValueChange = {
                if (!isMaxTagsReached) {
                    onAction(ImageDetailAction.OnNewTagTextChange(it))
                }
            },
            placeholder = stringResource(R.string.image_detail_tag_input_placeholder),
            errorMessage = state.tagErrorMessage,
            onClear = { onAction(ImageDetailAction.OnNewTagTextChange("")) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            enabled = !isMaxTagsReached,
            onDone = {
                onAction(ImageDetailAction.OnAddNewTag)
                focusManager.clearFocus()
            }
        )

        // 태그 목록
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.image_detail_added_tags),
                    style = subhead01Bold,
                    color = Text01
                )
                Text(
                    text = stringResource(R.string.image_detail_tag_max_info),
                    style = caption02Regular,
                    color = Text03
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                state.currentScreenshot.tags.forEach { tag ->
                    UiTagSelectedChip(
                        text = tag.name,
                        onDelete = { onAction(ImageDetailAction.OnTagDelete(tag)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imeVisible) {
            UiBottomInputButton(
                text = stringResource(R.string.common_complete),
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

    // 강제 포커스 제거 (→ 사용자가 눌러야 키보드 뜸)
    // 필요 시 직접 클릭으로 포커스 유도
}
*/
