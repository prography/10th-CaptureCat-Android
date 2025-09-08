package com.prography.tag

import com.prography.domain.model.TagWithCount

// Define UI State
data class TagSettingState(
    val tags: List<TagWithCount> = emptyList(),
    val tagCount: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTags: Set<String> = emptySet()
)

// Define Actions
sealed class TagSettingAction {
    object LoadTags : TagSettingAction()
    object ToggleEditMode : TagSettingAction()
    data class ToggleTagSelection(val tag: String) : TagSettingAction()
    object SelectAllTags : TagSettingAction()
    object ClearAllSelections : TagSettingAction()
    object DeleteSelectedTags : TagSettingAction()
    object DeleteAllTags : TagSettingAction()
}

// Define Effects
sealed class TagSettingEffect {
    data class ShowError(val message: String) : TagSettingEffect()
    data class ShowSuccess(val message: String) : TagSettingEffect()
}