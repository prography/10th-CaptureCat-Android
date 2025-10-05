package com.prography.tag

import androidx.lifecycle.viewModelScope
import com.prography.domain.model.TagModel
import com.prography.domain.usecase.tag.AddRecentTagUseCase
import com.prography.domain.usecase.tag.DeleteUserTagUseCase
import com.prography.domain.usecase.tag.GetUserTagsUseCase
import com.prography.domain.usecase.tag.UpdateUserTagUseCase
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import java.io.IOException

data class TagSettingUiState(
    val inputTag: String? = null,
    val tags: List<TagModel> = emptyList(),
    val tagCount: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTags: Set<String> = emptySet()
)

sealed class TagSettingEffect {
    data class ShowToast(val message: String) : TagSettingEffect()
    object NavigateBack : TagSettingEffect()
}

sealed class TagSettingAction {
    object LoadTags : TagSettingAction()
    object ToggleEditMode : TagSettingAction()
    data class ToggleTagSelection(val tag: String) : TagSettingAction()
    object SelectAllTags : TagSettingAction()
    object ClearAllSelections : TagSettingAction()
    data class AddInputTag(val tag: String) : TagSettingAction()
    object DeleteSelectedTags : TagSettingAction()
    data class UpdateTag(val tagId: Long, val newTagName: String) : TagSettingAction()
    object NavigateBack : TagSettingAction()
}

@HiltViewModel
class TagSettingViewModel @Inject constructor(
    private val getUserTagsUseCase: GetUserTagsUseCase,
    private val addUserTagsUseCase: AddRecentTagUseCase,
    private val deleteUserTagUseCase: DeleteUserTagUseCase,
    private val updateUserTagUseCase: UpdateUserTagUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<TagSettingUiState, TagSettingEffect, TagSettingAction>(TagSettingUiState()) {

    override fun handleAction(action: TagSettingAction) {
        when (action) {
            is TagSettingAction.LoadTags -> loadTags()
            is TagSettingAction.ToggleEditMode -> toggleEditMode()
            is TagSettingAction.ToggleTagSelection -> toggleTagSelection(action.tag)
            is TagSettingAction.SelectAllTags -> selectAllTags()
            is TagSettingAction.ClearAllSelections -> clearAllSelections()
            is TagSettingAction.AddInputTag -> addInputTag(action.tag)
            is TagSettingAction.DeleteSelectedTags -> deleteSelectedTags()
            is TagSettingAction.UpdateTag -> updateTag(action.tagId, action.newTagName)
            is TagSettingAction.NavigateBack -> navigationHelper.navigate(NavigationEvent.Up)
        }
    }

    fun loadTags() {
        viewModelScope.launch {
            showLoading()
            updateState { copy(isLoading = true) }

            try {
                getUserTagsUseCase().fold(
                    onSuccess = { tags ->
                        updateState {
                            copy(
                                tags = tags,
                                tagCount = tags.size,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        hideLoading()
                    },
                    onFailure = { e ->
                        val errorMessage = when (e) {
                            is IOException -> "네트워크 연결 오류입니다. 다시 시도해 주세요."
                            else -> "태그를 불러오는데 실패했습니다: ${e.message}"
                        }
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = errorMessage
                            )
                        }
                        hideLoading()
                        showToast(errorMessage)
                    }
                )
            } catch (e: Exception) {
                val errorMessage = "태그를 불러오는데 실패했습니다: ${e.message}"
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                hideLoading()
                showToast(errorMessage)
                Timber.e(e, "Exception while loading tags")
            }
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
            copy(selectedTags = tags.map { it.name }.toSet())
        }
    }

    private fun clearAllSelections() {
        updateState { copy(selectedTags = emptySet()) }
    }

    private fun addInputTag(text: String) {
        viewModelScope.launch {
            val t = text.trim()

            // 초기화
            updateState { copy(errorMessage = null) }

            when {
                t.isBlank() -> {
                    updateState { copy(errorMessage = "태그를 입력해 주세요.") }
                    return@launch
                }
                currentState.tags.any { it.name == t } -> {
                    updateState { copy(errorMessage = "동일한 태그가 이미 존재합니다.") }
                    return@launch
                }
                currentState.tagCount >= 30 -> {
                    updateState { copy(errorMessage = "최대 30개까지 등록 가능해요.") }
                    return@launch
                }
            }

            showLoading()
            try {
                val added = addUserTagsUseCase(t).first()
                val updated = currentState.tags + added
                updateState {
                    copy(
                        tags = updated,
                        tagCount = updated.size,
                        inputTag = "",      // 입력칸 비우기
                        errorMessage = null // 성공 시 에러 초기화
                    )
                }
            } catch (e: Throwable) {
                updateState { copy(errorMessage = e.message ?: "등록 실패") }
                Timber.e(e, "addInputTag failed")
            } finally {
                hideLoading()
                showToast("새로운 태그가 등록되었어요")
            }
        }
    }


    private fun deleteSelectedTags() {
        viewModelScope.launch {
            showLoading()
            updateState { copy(isLoading = true) }

            try {
                val selectedTagIds = currentState.tags
                    .filter { currentState.selectedTags.contains(it.name) }
                    .mapNotNull { it.id }

                if (selectedTagIds.isEmpty()) {
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = "삭제할 태그가 선택되지 않았습니다."
                        )
                    }
                    hideLoading()
                    showToast("삭제할 태그가 선택되지 않았습니다.")
                    return@launch
                }

                val results = deleteUserTagUseCase.deleteMultipleTags(selectedTagIds)

                val hasFailure = results.any { it.isFailure }

                if (hasFailure) {
                    showToast("일부 태그 삭제에 실패했습니다.")
                } else {
                    val updatedTags = currentState.tags.filter { tag ->
                        !currentState.selectedTags.contains(tag.name)
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
                    hideLoading()
                    showToast("태그가 삭제되었어요")
                }
            } catch (e: Exception) {
                val errorMessage = "태그 삭제에 실패했습니다: ${e.message}"
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                hideLoading()
                showToast(errorMessage)
                Timber.e(e, "Exception while deleting tags")
            }
        }
    }

    private fun updateTag(tagId: Long, newTagName: String) {
        if (newTagName.isBlank()) {
            showToast("태그 이름을 입력해주세요.")
            return
        }

        viewModelScope.launch {
            showLoading()
            updateState { copy(isLoading = true) }

            try {
                updateUserTagUseCase(tagId, newTagName).fold(
                    onSuccess = { _ ->
                        loadTags()
                        hideLoading()
                        showToast("태그가 수정되었습니다.")
                    },
                    onFailure = { e ->
                        val errorMessage = when (e) {
                            is IOException -> "네트워크 연결 오류입니다. 다시 시도해 주세요."
                            else -> "태그 수정에 실패했습니다: ${e.message}"
                        }
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = errorMessage
                            )
                        }
                        hideLoading()
                        showToast(errorMessage)
                    }
                )
            } catch (e: Exception) {
                val errorMessage = "태그 수정에 실패했습니다: ${e.message}"
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                hideLoading()
                showToast(errorMessage)
                Timber.e(e, "Exception while updating tag")
            }
        }
    }

    init {
        loadTags()
    }
}