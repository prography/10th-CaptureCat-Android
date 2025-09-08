package com.prography.tag

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.GetMostUsedTagsUseCase
import com.prography.domain.usecase.screenshot.DeleteTagsUseCase
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TagSettingViewModel @Inject constructor(
    private val getMostUsedTagsUseCase: GetMostUsedTagsUseCase,
    private val deleteTagsUseCase: DeleteTagsUseCase
) : BaseComposeViewModel<TagSettingState, TagSettingEffect, TagSettingAction>(TagSettingState()) {

    fun loadTags() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val tags = getMostUsedTagsUseCase(30)
                updateState {
                    copy(
                        tags = tags,
                        tagCount = tags.size,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                emitEffect(TagSettingEffect.ShowError(e.message ?: "태그 로드에 실패했습니다."))
                Timber.e(e, "Failed to load tags")
            }
        }
    }

    override fun handleAction(action: TagSettingAction) {
        when (action) {
            TagSettingAction.LoadTags -> loadTags()
            TagSettingAction.ToggleEditMode -> toggleEditMode()
            is TagSettingAction.ToggleTagSelection -> toggleTagSelection(action.tag)
            TagSettingAction.SelectAllTags -> selectAllTags()
            TagSettingAction.ClearAllSelections -> clearAllSelections()
            TagSettingAction.DeleteSelectedTags -> deleteSelectedTags()
            TagSettingAction.DeleteAllTags -> deleteAllTags()
        }
    }

    private fun toggleEditMode() {
        updateState {
            copy(
                isEditMode = !isEditMode,
                selectedTags = emptySet()
            )
        }
    }

    private fun toggleTagSelection(tag: String) {
        updateState {
            copy(
                selectedTags = selectedTags.let {
                    if (it.contains(tag)) it - tag else it + tag
                }
            )
        }
    }

    private fun selectAllTags() {
        updateState {
            copy(selectedTags = tags.map { it.tag }.toSet())
        }
    }

    private fun clearAllSelections() {
        updateState {
            copy(selectedTags = emptySet())
        }
    }

    private fun deleteSelectedTags() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val selectedTagIds = currentState.tags
                    .filter { currentState.selectedTags.contains(it.tag) }
                    .mapNotNull { it.id }

                if (selectedTagIds.isNotEmpty()) {
                    Result.runCatching { deleteTagsUseCase(selectedTagIds) }
                        .onSuccess {
                            val updatedTags = currentState.tags.filter {
                                !currentState.selectedTags.contains(it.tag)
                            }
                            updateState {
                                copy(
                                    tags = updatedTags,
                                    tagCount = updatedTags.size,
                                    selectedTags = emptySet(),
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                            emitEffect(TagSettingEffect.ShowSuccess("선택된 태그가 삭제되었습니다."))
                            Timber.d("Selected tags deleted successfully")
                        }
                        .onFailure {
                            updateState {
                                copy(
                                    isLoading = false,
                                    errorMessage = "선택된 태그 삭제에 실패했습니다."
                                )
                            }
                            emitEffect(TagSettingEffect.ShowError("선택된 태그 삭제에 실패했습니다."))
                            Timber.e(it, "Failed to delete selected tags")
                        }
                } else {
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = "삭제할 태그가 선택되지 않았습니다."
                        )
                    }
                    emitEffect(TagSettingEffect.ShowError("삭제할 태그가 선택되지 않았습니다."))
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                emitEffect(TagSettingEffect.ShowError(e.message ?: "선택된 태그 삭제 중 오류가 발생했습니다."))
                Timber.e(e, "Exception while deleting selected tags")
            }
        }
    }

    private fun deleteAllTags() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                Result.runCatching { deleteTagsUseCase.deleteAllTags() }
                    .onSuccess {
                        updateState {
                            copy(
                                tags = emptyList(),
                                tagCount = 0,
                                selectedTags = emptySet(),
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        emitEffect(TagSettingEffect.ShowSuccess("모든 태그가 삭제되었습니다."))
                        Timber.d("All tags deleted successfully")
                    }
                    .onFailure {
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = "전체 태그 삭제에 실패했습니다."
                            )
                        }
                        emitEffect(TagSettingEffect.ShowError("전체 태그 삭제에 실패했습니다."))
                        Timber.e(it, "Failed to delete all tags")
                    }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                emitEffect(TagSettingEffect.ShowError(e.message ?: "전체 태그 삭제 중 오류가 발생했습니다."))
                Timber.e(e, "Exception while deleting all tags")
            }
        }
    }
}