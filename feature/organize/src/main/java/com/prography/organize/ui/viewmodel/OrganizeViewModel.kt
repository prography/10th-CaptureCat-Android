package com.prography.organize.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.BulkInsertScreenshotUseCase
import com.prography.domain.usecase.tag.AddRecentTagUseCase
import com.prography.domain.usecase.tag.GetRecentTagsUseCase
import com.prography.domain.model.UiScreenshotModel
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeEffect
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.contract.OrganizeState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.prography.ui.common.ToastType
import com.prography.domain.model.TagModel
import java.util.UUID
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class OrganizeViewModel @Inject constructor(
    private val bulkInsertScreenshotUseCase: BulkInsertScreenshotUseCase,
    private val getRecentTagsUseCase: GetRecentTagsUseCase,
    private val addRecentTagUseCase: AddRecentTagUseCase,
    @ApplicationContext private val context: Context
) : BaseComposeViewModel<OrganizeState, OrganizeEffect, OrganizeAction>(
    initialState = OrganizeState()
) {

    init {
        loadRecentTags()
    }

    fun initializeScreenshots(screenshots: List<OrganizeScreenshotItem>, currentIndex: Int = 0) {
        Timber.d("Initializing ${screenshots.size} screenshots with currentIndex: $currentIndex")
        updateState {
            copy(
                screenshots = screenshots,
                currentIndex = currentIndex,
                availableTags = getAvailableTags()
            )
        }
        Timber.d("Screenshots initialized successfully")
    }

    override fun handleAction(action: OrganizeAction) {
        when (action) {
            OrganizeAction.OnNavigateUp -> {
                emitEffect(OrganizeEffect.NavigateUp)
            }

            is OrganizeAction.OnModeChange -> {
                updateState {
                    copy(organizeMode = action.mode)
                }
            }

            is OrganizeAction.OnScreenshotDelete -> {
                deleteScreenshot(action.screenshotId)
            }

            is OrganizeAction.OnFavoriteToggle -> {
                updateScreenshotFavorite(action.screenshotId, action.isFavorite)
            }

            is OrganizeAction.OnTagToggle -> {
                toggleScreenshotTag(action.screenshotId, action.tagText)
            }

            is OrganizeAction.OnAddTag -> {
                emitEffect(OrganizeEffect.ShowAddTagBottomSheet(action.screenshotId))
            }

            is OrganizeAction.OnCreateNewTag -> {
                addNewTagToScreenshot(action.screenshotId, action.tagText)
            }

            is OrganizeAction.OnPageChange -> {
                updateState {
                    copy(currentIndex = action.newIndex)
                }
            }

            OrganizeAction.OnSaveScreenshots -> {
                saveScreenshots()
            }

            OrganizeAction.OnCompletionNext -> {
                // 완료 화면에서 다음 버튼 클릭 시 실제 완료 처리
                emitEffect(OrganizeEffect.NavigateToComplete)
            }
        }
    }

    private fun deleteScreenshot(screenshotId: String) {
        Timber.d("Deleting screenshot with ID: $screenshotId")
        updateState {
            val newScreenshots = screenshots.filter { it.id != screenshotId }
            val newIndex = if (newScreenshots.isEmpty()) {
                0
            } else {
                currentIndex.coerceAtMost(newScreenshots.size - 1)
            }

            if (newScreenshots.isEmpty()) {
                Timber.i("All screenshots deleted, navigating to complete")
                emitEffect(OrganizeEffect.NavigateToComplete)
            } else {
                Timber.d("Screenshot deleted. Remaining: ${newScreenshots.size}, newIndex: $newIndex")
            }

            copy(
                screenshots = newScreenshots,
                currentIndex = newIndex
            )
        }
    }

    private fun updateScreenshotFavorite(screenshotId: String, isFavorite: Boolean) {
        Timber.d("Updating favorite status for screenshot $screenshotId to $isFavorite")
        updateState {
            val updatedScreenshots = screenshots.map { screenshot ->
                if (screenshot.id == screenshotId) {
                    screenshot.copy(isFavorite = isFavorite)
                } else {
                    screenshot
                }
            }
            copy(screenshots = updatedScreenshots)
        }
        Timber.d("Favorite status updated successfully")
    }

    private fun toggleScreenshotTag(screenshotId: String, tagText: String) {
        val currentTags = getCurrentScreenshotTags()
        val hadTag = currentTags.any { it.name == tagText }
        if (hadTag) {
            updateState {
                val updatedScreenshots = when (organizeMode) {
                    OrganizeMode.BATCH -> {
                        screenshots.map { screenshot ->
                            val updatedTags = screenshot.tags.filterNot { it.name == tagText }
                            screenshot.copy(tags = updatedTags)
                        }
                    }
                    OrganizeMode.SINGLE -> {
                        screenshots.map { screenshot ->
                            if (screenshot.id == screenshotId) {
                                val updatedTags = screenshot.tags.filterNot { it.name == tagText }
                                screenshot.copy(tags = updatedTags)
                            } else screenshot
                        }
                    }
                }
                copy(screenshots = updatedScreenshots)
            }
        } else if (currentTags.size >= 4) {
            showToast("태그는 최대 4개까지 지정할 수 있어요.", ToastType.Default)
        } else {
            val newTagModel = TagModel(UUID.randomUUID().toString(), tagText)
            updateState {
                val updatedScreenshots = when (organizeMode) {
                    OrganizeMode.BATCH -> {
                        screenshots.map { screenshot ->
                            if (screenshot.tags.any { it.name == tagText }) screenshot
                            else screenshot.copy(tags = screenshot.tags + newTagModel)
                        }
                    }
                    OrganizeMode.SINGLE -> {
                        screenshots.map { screenshot ->
                            if (screenshot.id == screenshotId && !screenshot.tags.any { it.name == tagText }) {
                                screenshot.copy(tags = screenshot.tags + newTagModel)
                            } else screenshot
                        }
                    }
                }
                copy(screenshots = updatedScreenshots)
            }
        }
    }

    private fun addNewTagToScreenshot(screenshotId: String, tagText: String) {
        updateState {
            val newAvailableTags = if (!availableTags.contains(tagText)) {
                listOf(tagText) + availableTags
            } else availableTags
            copy(availableTags = newAvailableTags)
        }
        toggleScreenshotTag(screenshotId, tagText)
        viewModelScope.launch {
            try {
                addRecentTagUseCase(tagText)
            } catch (_: Exception) { }
        }
    }

    private fun saveScreenshots() {
        val screenshotsToSave = currentState.screenshots
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            runCatching {
                val uiScreenshots = screenshotsToSave.map { screenshot ->
                    val now = Date()
                    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                    val dateTaken = getMediaStoreDateTaken(context, screenshot.uri.toString())
                    val dateStr =
                        if (dateTaken != null) dateFormat.format(Date(dateTaken))
                        else parseDateFromFileName(screenshot.fileName).ifBlank {
                            dateFormat.format(
                                now
                            )
                        }
                    UiScreenshotModel(
                        id = screenshot.id,
                        uri = screenshot.uri.toString(),
                        tags = screenshot.tags,
                        isFavorite = screenshot.isFavorite,
                        dateStr = dateStr
                    )
                }
                bulkInsertScreenshotUseCase(uiScreenshots)
            }.onSuccess {
                updateState { copy(showCompletionMessage = true) }
            }.onFailure {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun getAvailableTags(): List<String> {
        return listOf(
            "쇼핑", "직무 관련", "레퍼런스"
        )
    }

    private fun loadRecentTags() {
        viewModelScope.launch {
            try {
                val recentTags = getRecentTagsUseCase().first()
                if (recentTags.isNotEmpty()) {
                    updateState { copy(availableTags = recentTags) }
                    Timber.d("Loaded recent tags: $recentTags")
                } else {
                    // 기본 태그 사용
                    updateState { copy(availableTags = getAvailableTags()) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load recent tags")
                // 기본 태그 사용
                updateState { copy(availableTags = getAvailableTags()) }
            }
        }
    }

    private fun getMediaStoreDateTaken(context: Context, uriString: String): Long? {
        return try {
            val projection = arrayOf(MediaStore.Images.Media.DATE_TAKEN)
            val uri = Uri.parse(uriString)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val dateTaken = cursor.getLong(idx)
                    if (dateTaken > 0) dateTaken else null
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    // 파일 이름으로 부터 날짜 추출
    private fun parseDateFromFileName(fileName: String?): String {
        if (fileName.isNullOrBlank()) return ""

        val nameWithoutExtension = fileName.substringBeforeLast(".")
        val datePatterns = listOf(
            Regex("""\d{8}_\d{6}"""),        // 20231215_143022
            Regex("""\d{4}-\d{2}-\d{2}"""),  // 2023-12-15
            Regex("""\d{4}\d{2}\d{2}"""),    // 20231215
        )

        val match = datePatterns.firstNotNullOfOrNull { regex ->
            regex.find(nameWithoutExtension)?.value
        } ?: return ""

        val rawDateStr = when {
            match.contains("_") -> match.split("_").first() // 20231215
            match.contains("-") -> match.replace("-", "")   // 2023-12-15 → 20231215
            else -> match
        }

        return try {
            val parsed = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(rawDateStr)
            val outputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
            outputFormat.format(parsed!!)
        } catch (e: Exception) {
            ""
        }
    }

    // 현재 스크린샷의 태그를 가져오는 헬퍼 함수
    fun getCurrentScreenshotTags(): List<TagModel> {
        return when (currentState.organizeMode) {
            OrganizeMode.BATCH -> {
                if (currentState.screenshots.isEmpty()) emptyList() else {
                    val allTags = currentState.screenshots.flatMap { it.tags }.distinctBy { it.name }
                    allTags.filter { tag -> currentState.screenshots.all { screenshot -> screenshot.tags.any { it.name == tag.name } } }
                }
            }
            OrganizeMode.SINGLE -> {
                currentState.screenshots.getOrNull(currentState.currentIndex)?.tags ?: emptyList()
            }
        }
    }

    // 현재 컨텍스트에서 사용할 스크린샷 ID
    fun getCurrentScreenshotId(): String {
        val state = currentState
        return when (state.organizeMode) {
            OrganizeMode.BATCH -> "all" // 한번에 모드는 특별한 ID 사용
            OrganizeMode.SINGLE -> {
                state.screenshots.getOrNull(state.currentIndex)?.id ?: ""
            }
        }
    }
}