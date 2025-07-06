package com.prography.imageDetail.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.screenshot.DeleteScreenshotUseCase
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.UpdateScreenshotUseCase
import com.prography.imageDetail.ui.contract.ImageDetailAction
import com.prography.imageDetail.ui.contract.ImageDetailEffect
import com.prography.imageDetail.ui.contract.ImageDetailState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val getAllScreenshotsUseCase: GetAllScreenshotsUseCase,
    private val deleteScreenshotUseCase: DeleteScreenshotUseCase,
    private val updateScreenshotUseCase: UpdateScreenshotUseCase
) : BaseComposeViewModel<ImageDetailState, ImageDetailEffect, ImageDetailAction>(
    initialState = ImageDetailState()
) {

    fun initializeScreenshots(screenshots: List<UiScreenshotModel>, currentIndex: Int = 0) {
        Timber.d("Initializing ${screenshots.size} screenshots with currentIndex: $currentIndex")
        updateState {
            copy(
                screenshots = screenshots,
                currentIndex = currentIndex,
                currentScreenshot = screenshots.getOrNull(currentIndex),
                availableTags = getAvailableTags()
            )
        }
        Timber.d("Screenshots initialized successfully")
    }

    override fun handleAction(action: ImageDetailAction) {
        when (action) {
            ImageDetailAction.OnNavigateBack -> {
                emitEffect(ImageDetailEffect.NavigateBack)
            }

            is ImageDetailAction.OnPageChange -> {
                updateState {
                    copy(
                        currentIndex = action.newIndex,
                        currentScreenshot = screenshots.getOrNull(action.newIndex)
                    )
                }
            }

            ImageDetailAction.OnToggleFavorite -> {
                toggleFavorite()
            }

            ImageDetailAction.OnShowTagEditBottomSheet -> {
                updateState {
                    copy(isTagEditBottomSheetVisible = true)
                }
            }

            ImageDetailAction.OnHideTagEditBottomSheet -> {
                updateState {
                    copy(
                        isTagEditBottomSheetVisible = false,
                        newTagText = ""
                    )
                }
            }

            is ImageDetailAction.OnTagDelete -> {
                deleteTag(action.tag)
            }

            is ImageDetailAction.OnNewTagTextChange -> {
                updateState {
                    copy(newTagText = action.text)
                }
            }

            ImageDetailAction.OnAddNewTag -> {
                addNewTag()
            }

            ImageDetailAction.OnDeleteScreenshot -> {
                updateState {
                    copy(isDeleteDialogVisible = true)
                }
            }

            ImageDetailAction.OnShowDeleteDialog -> {
                updateState {
                    copy(isDeleteDialogVisible = true)
                }
            }

            ImageDetailAction.OnHideDeleteDialog -> {
                updateState {
                    copy(isDeleteDialogVisible = false)
                }
            }

            ImageDetailAction.OnConfirmDelete -> {
                updateState {
                    copy(isDeleteDialogVisible = false)
                }
                deleteCurrentScreenshot()
            }
        }
    }

    private fun toggleFavorite() {
        val currentScreenshot = currentState.currentScreenshot ?: return
        val updatedScreenshot = currentScreenshot.copy(isFavorite = !currentScreenshot.isFavorite)

        updateState {
            val updatedScreenshots = screenshots.map { screenshot ->
                if (screenshot.id == currentScreenshot.id) {
                    updatedScreenshot
                } else {
                    screenshot
                }
            }
            copy(
                screenshots = updatedScreenshots,
                currentScreenshot = updatedScreenshot
            )
        }

        viewModelScope.launch {
            runCatching {
                updateScreenshotUseCase(updatedScreenshot)
                Timber.d("Successfully updated favorite status for screenshot: ${updatedScreenshot.id}")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to update favorite status")
                emitEffect(ImageDetailEffect.ShowError("즐겨찾기 업데이트에 실패했습니다."))
            }
        }
    }

    private fun deleteTag(tag: String) {
        val currentScreenshot = currentState.currentScreenshot ?: return
        val updatedTags = currentScreenshot.tags.filter { it != tag }
        val updatedScreenshot = currentScreenshot.copy(tags = updatedTags)

        updateState {
            val updatedScreenshots = screenshots.map { screenshot ->
                if (screenshot.id == currentScreenshot.id) {
                    updatedScreenshot
                } else {
                    screenshot
                }
            }
            copy(
                screenshots = updatedScreenshots,
                currentScreenshot = updatedScreenshot
            )
        }

        viewModelScope.launch {
            runCatching {
                updateScreenshotUseCase(updatedScreenshot)
                Timber.d("Successfully deleted tag '$tag' from screenshot: ${updatedScreenshot.id}")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to delete tag")
                emitEffect(ImageDetailEffect.ShowError("태그 삭제에 실패했습니다."))
            }
        }
    }

    private fun addNewTag() {
        val newTag = currentState.newTagText.trim()
        if (newTag.isEmpty()) return

        val currentScreenshot = currentState.currentScreenshot ?: return
        val updatedTags = currentScreenshot.tags + newTag
        val updatedScreenshot = currentScreenshot.copy(tags = updatedTags)

        updateState {
            val updatedScreenshots = screenshots.map { screenshot ->
                if (screenshot.id == currentScreenshot.id) {
                    updatedScreenshot
                } else {
                    screenshot
                }
            }
            copy(
                screenshots = updatedScreenshots,
                currentScreenshot = updatedScreenshot,
                newTagText = "",
                availableTags = if (!availableTags.contains(newTag)) {
                    availableTags + newTag
                } else {
                    availableTags
                }
            )
        }

        viewModelScope.launch {
            runCatching {
                updateScreenshotUseCase(updatedScreenshot)
                Timber.d("Successfully added tag '$newTag' to screenshot: ${updatedScreenshot.id}")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to add tag")
                emitEffect(ImageDetailEffect.ShowError("태그 추가에 실패했습니다."))
            }
        }
    }

    private fun deleteCurrentScreenshot() {
        val currentScreenshot = currentState.currentScreenshot ?: return

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            runCatching {
                deleteScreenshotUseCase(currentScreenshot.id)
            }.onSuccess {
                val updatedScreenshots =
                    currentState.screenshots.filter { it.id != currentScreenshot.id }

                if (updatedScreenshots.isEmpty()) {
                    emitEffect(ImageDetailEffect.NavigateBack)
                } else {
                    val newIndex =
                        currentState.currentIndex.coerceAtMost(updatedScreenshots.size - 1)
                    updateState {
                        copy(
                            screenshots = updatedScreenshots,
                            currentIndex = newIndex,
                            currentScreenshot = updatedScreenshots.getOrNull(newIndex),
                            isLoading = false
                        )
                    }
                    emitEffect(ImageDetailEffect.ScreenshotDeleted)
                }
            }.onFailure { exception ->
                Timber.e(exception, "Failed to delete screenshot")
                emitEffect(ImageDetailEffect.ShowError("스크린샷 삭제에 실패했습니다."))
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun getAvailableTags(): List<String> {
        return listOf(
            "쇼핑", "직무 관련", "레퍼런스", "여행", "음식", "패션", "생활"
        )
    }
}