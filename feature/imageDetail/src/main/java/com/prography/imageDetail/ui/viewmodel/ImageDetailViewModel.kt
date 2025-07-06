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

    private var screenshotIds: List<String> = emptyList()
    private val screenshotCache = mutableMapOf<String, UiScreenshotModel>()

    fun initializeWithIds(screenshotIds: List<String>, currentIndex: Int = 0) {
        Timber.d("Initializing with ${screenshotIds.size} screenshot IDs, currentIndex: $currentIndex")
        println("DEBUG: ImageDetailViewModel - initializeWithIds called with currentIndex: $currentIndex, screenshotIds: $screenshotIds")

        this.screenshotIds = screenshotIds

        updateState {
            copy(
                currentIndex = currentIndex,
                // Create placeholder screenshots to maintain correct pager count
                screenshots = screenshotIds.map { id ->
                    UiScreenshotModel(
                        id = id,
                        uri = "",
                        appName = "",
                        tags = emptyList(),
                        isFavorite = false,
                        dateStr = ""
                    )
                },
                availableTags = getAvailableTags(),
                isLoading = true
            )
        }

        println("DEBUG: State updated with currentIndex: ${currentState.currentIndex}")

        // Load current screenshot first for immediate display
        loadScreenshotById(screenshotIds.getOrNull(currentIndex) ?: "", currentIndex)

        // Then load all screenshots in background
        loadAllScreenshots()
    }

    private fun loadScreenshotById(screenshotId: String, index: Int) {
        if (screenshotId.isEmpty()) return

        viewModelScope.launch {
            try {
                // Always load from database to get the latest data
                Timber.d("Loading screenshot from database: $screenshotId")
                val allScreenshots = getAllScreenshotsUseCase().firstOrNull() ?: emptyList()
                val screenshot = allScreenshots.find { it.id == screenshotId }

                if (screenshot != null) {
                    // Update cache with latest data
                    screenshotCache[screenshotId] = screenshot

                    // Update UI immediately
                    updateCurrentScreenshot(screenshot, index)
                    updateScreenshotsListAtIndex(index, screenshot)

                    Timber.d("Successfully loaded and cached screenshot: ${screenshot.id}")
                } else {
                    Timber.w("Screenshot not found for ID: $screenshotId")
                    emitEffect(ImageDetailEffect.ShowError("스크린샷을 찾을 수 없습니다."))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load screenshot: $screenshotId")
                emitEffect(ImageDetailEffect.ShowError("스크린샷을 불러오는데 실패했습니다."))
            }
        }
    }

    private fun loadAllScreenshots() {
        viewModelScope.launch {
            try {
                val allScreenshots = getAllScreenshotsUseCase().firstOrNull() ?: emptyList()

                // Cache all relevant screenshots
                val loadedScreenshots = mutableListOf<UiScreenshotModel>()
                screenshotIds.forEachIndexed { index, id ->
                    val screenshot = allScreenshots.find { it.id == id }
                    if (screenshot != null) {
                        screenshotCache[id] = screenshot
                        loadedScreenshots.add(screenshot)
                    } else {
                        // Keep placeholder for missing screenshots
                        loadedScreenshots.add(
                            UiScreenshotModel(
                                id = id,
                                uri = "",
                                appName = "",
                                tags = emptyList(),
                                isFavorite = false,
                                dateStr = ""
                            )
                        )
                        Timber.w("Screenshot not found in database: $id")
                    }
                }

                // Update the complete screenshots list and current screenshot
                val currentScreenshot = loadedScreenshots.getOrNull(currentState.currentIndex)
                updateState {
                    copy(
                        screenshots = loadedScreenshots,
                        currentScreenshot = currentScreenshot,
                        isLoading = false
                    )
                }

                Timber.d("Successfully loaded ${screenshotCache.size} screenshots from ${screenshotIds.size} IDs")
                Timber.d("Current screenshot set to: ${currentScreenshot?.id}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load all screenshots")
                updateState { copy(isLoading = false) }
                emitEffect(ImageDetailEffect.ShowError("스크린샷을 불러오는데 실패했습니다."))
            }
        }
    }

    private fun updateCurrentScreenshot(screenshot: UiScreenshotModel, index: Int) {
        println("DEBUG: updateCurrentScreenshot called with index: $index, screenshot.id: ${screenshot.id}")
        updateState {
            copy(
                currentScreenshot = screenshot,
                currentIndex = index
            )
        }
        println("DEBUG: After updateCurrentScreenshot - currentIndex: ${currentState.currentIndex}")
    }

    override fun handleAction(action: ImageDetailAction) {
        when (action) {
            ImageDetailAction.OnNavigateBack -> {
                emitEffect(ImageDetailEffect.NavigateBack)
            }

            is ImageDetailAction.OnPageChange -> {
                val newIndex = action.newIndex
                Timber.d("Page changed to index: $newIndex")
                println("DEBUG: OnPageChange - newIndex: $newIndex, current screenshots size: ${currentState.screenshots.size}")
                println("DEBUG: Current currentScreenshot before change: ${currentState.currentScreenshot?.id}")

                // Update index immediately
                updateState {
                    copy(currentIndex = newIndex)
                }

                // Get the screenshot ID for the new page
                val screenshotId = screenshotIds.getOrNull(newIndex)
                if (screenshotId != null) {
                    // Check if we already have this screenshot in our current list
                    val existingScreenshot = currentState.screenshots.getOrNull(newIndex)
                    println("DEBUG: existingScreenshot at index $newIndex: ${existingScreenshot?.id}, uri empty: ${existingScreenshot?.uri?.isEmpty()}")

                    if (existingScreenshot != null && existingScreenshot.uri.isNotEmpty()) {
                        // Use existing screenshot if it's already loaded
                        println("DEBUG: BEFORE updating currentScreenshot to: ${existingScreenshot.id}, tags: ${existingScreenshot.tags}")
                        updateState {
                            copy(currentScreenshot = existingScreenshot)
                        }
                        println("DEBUG: AFTER updating currentScreenshot to: ${currentState.currentScreenshot?.id}, tags: ${currentState.currentScreenshot?.tags}")
                        Timber.d("Updated currentScreenshot from existing list: ${existingScreenshot.id}")
                    } else {
                        // Load from database if not available
                        println("DEBUG: Loading screenshot from database for index: $newIndex, id: $screenshotId")
                        loadScreenshotById(screenshotId, newIndex)
                    }
                } else {
                    println("DEBUG: No screenshot ID found for index: $newIndex")
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

    private fun updateScreenshotsListAtIndex(index: Int, screenshot: UiScreenshotModel?) {
        if (screenshot == null) return

        updateState {
            val updatedScreenshots = screenshots.toMutableList()
            if (index < updatedScreenshots.size) {
                updatedScreenshots[index] = screenshot
            }
            copy(screenshots = updatedScreenshots)
        }
    }

    private fun toggleFavorite() {
        val currentScreenshot = currentState.currentScreenshot ?: return
        val updatedScreenshot = currentScreenshot.copy(isFavorite = !currentScreenshot.isFavorite)

        // Update cache first
        screenshotCache[updatedScreenshot.id] = updatedScreenshot

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

        // Save to database
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

        // Update cache first
        screenshotCache[updatedScreenshot.id] = updatedScreenshot

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

        // Save to database
        viewModelScope.launch {
            runCatching {
                updateScreenshotUseCase(updatedScreenshot)
                Timber.d("Successfully deleted tag '$tag' from screenshot: ${updatedScreenshot.id}")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to delete tag")
                // Revert cache on failure
                screenshotCache[currentScreenshot.id] = currentScreenshot
                updateState {
                    val revertedScreenshots = screenshots.map { screenshot ->
                        if (screenshot.id == currentScreenshot.id) {
                            currentScreenshot
                        } else {
                            screenshot
                        }
                    }
                    copy(
                        screenshots = revertedScreenshots,
                        currentScreenshot = currentScreenshot
                    )
                }
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

        // Update cache first
        screenshotCache[updatedScreenshot.id] = updatedScreenshot

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

        // Save to database
        viewModelScope.launch {
            runCatching {
                updateScreenshotUseCase(updatedScreenshot)
                Timber.d("Successfully added tag '$newTag' to screenshot: ${updatedScreenshot.id}")
            }.onFailure { exception ->
                Timber.e(exception, "Failed to add tag")
                // Revert cache on failure
                screenshotCache[currentScreenshot.id] = currentScreenshot
                updateState {
                    val revertedScreenshots = screenshots.map { screenshot ->
                        if (screenshot.id == currentScreenshot.id) {
                            currentScreenshot
                        } else {
                            screenshot
                        }
                    }
                    copy(
                        screenshots = revertedScreenshots,
                        currentScreenshot = currentScreenshot,
                        newTagText = "",
                        availableTags = if (availableTags.contains(newTag)) {
                            availableTags - newTag
                        } else {
                            availableTags
                        }
                    )
                }
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
                // Remove from cache
                screenshotCache.remove(currentScreenshot.id)

                // Remove from IDs list
                screenshotIds = screenshotIds.filter { it != currentScreenshot.id }

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