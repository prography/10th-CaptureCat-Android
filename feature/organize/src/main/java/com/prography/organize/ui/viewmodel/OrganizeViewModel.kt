package com.prography.organize.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.BulkInsertScreenshotUseCase
import com.prography.domain.model.UiScreenshotModel
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.contract.OrganizeAction
import com.prography.organize.ui.contract.OrganizeEffect
import com.prography.organize.ui.contract.OrganizeMode
import com.prography.organize.ui.contract.OrganizeState
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrganizeViewModel @Inject constructor(
    private val bulkInsertScreenshotUseCase: BulkInsertScreenshotUseCase
) : BaseComposeViewModel<OrganizeState, OrganizeEffect, OrganizeAction>(
    initialState = OrganizeState()
) {

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
        Timber.d("Toggling tag '$tagText' for screenshot $screenshotId in ${currentState.organizeMode} mode")
        updateState {
            val updatedScreenshots = when (organizeMode) {
                OrganizeMode.BATCH -> {
                    Timber.d("BATCH MODE: Applying tag '$tagText' to ALL ${screenshots.size} screenshots")

                    // 한번에 모드: 모든 스크린샷에 동일한 태그 적용
                    val result = screenshots.mapIndexed { index, screenshot ->
                        val currentTags = screenshot.tags.toMutableList()
                        val hadTag = currentTags.contains(tagText)

                        if (hadTag) {
                            currentTags.remove(tagText)
                            Timber.d("  Screenshot ${index + 1}: Removed tag '$tagText' - remaining tags: $currentTags")
                        } else {
                            if (currentTags.size < 4) { // 최대 4개 제한
                                currentTags.add(tagText)
                                Timber.d("  Screenshot ${index + 1}: Added tag '$tagText' - new tags: $currentTags")
                            } else {
                                Timber.w("  Screenshot ${index + 1}: Cannot add tag '$tagText' - already has 4 tags: $currentTags")
                            }
                        }
                        screenshot.copy(tags = currentTags)
                    }

                    // 모든 스크린샷이 동일한 태그를 가지는지 검증
                    val allTagsAreSame = result.map { it.tags.sorted() }.distinct().size == 1
                    Timber.d("BATCH MODE RESULT: All screenshots have same tags? $allTagsAreSame")
                    if (allTagsAreSame && result.isNotEmpty()) {
                        Timber.d("All screenshots now have tags: ${result.first().tags}")
                    }

                    result
                }
                OrganizeMode.SINGLE -> {
                    Timber.d("SINGLE MODE: Applying tag '$tagText' to screenshot $screenshotId only")

                    // 한장씩 모드: 현재 스크린샷에만 태그 적용
                    screenshots.mapIndexed { index, screenshot ->
                        if (screenshot.id == screenshotId) {
                            val currentTags = screenshot.tags.toMutableList()
                            val hadTag = currentTags.contains(tagText)

                            if (hadTag) {
                                currentTags.remove(tagText)
                                Timber.d("  Current screenshot: Removed tag '$tagText' - remaining tags: $currentTags")
                            } else {
                                if (currentTags.size < 4) { // 최대 4개 제한
                                    currentTags.add(tagText)
                                    Timber.d("  Current screenshot: Added tag '$tagText' - new tags: $currentTags")
                                } else {
                                    Timber.w("  Current screenshot: Cannot add tag '$tagText' - already has 4 tags: $currentTags")
                                }
                            }
                            screenshot.copy(tags = currentTags)
                        } else {
                            // 다른 스크린샷은 변경하지 않음
                            screenshot
                        }
                    }
                }
            }
            copy(screenshots = updatedScreenshots)
        }
        Timber.d("Tag toggle completed successfully")
    }

    private fun addNewTagToScreenshot(screenshotId: String, tagText: String) {
        Timber.d("Adding new tag '$tagText' for screenshot $screenshotId")
        // 사용 가능한 태그 목록에 추가
        updateState {
            val newAvailableTags = if (!availableTags.contains(tagText)) {
                listOf(tagText) + availableTags
            } else {
                availableTags
            }
            copy(availableTags = newAvailableTags)
        }

        // 해당 스크린샷에 태그 추가
        toggleScreenshotTag(screenshotId, tagText)
        Timber.d("New tag '$tagText' added successfully")
    }

    private fun saveScreenshots() {
        val screenshotsToSave = currentState.screenshots
        Timber.d("Starting to save ${screenshotsToSave.size} screenshots")

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            runCatching {
                val uiScreenshots = screenshotsToSave.map { screenshot ->
                    val parsedAppName = parseAppNameFromFileName(screenshot.fileName)
                    Timber.d("Parsed app name: '$parsedAppName' from filename: ${screenshot.fileName}")

                    // 날짜 파싱 못하면 현재 날짜로 파싱
                    val now = Date()
                    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                    val dateStr = parseDateFromFileName(screenshot.fileName).ifBlank {
                        dateFormat.format(now)
                    }

                    UiScreenshotModel(
                        id = screenshot.id,
                        uri = screenshot.uri.toString(),
                        appName = parsedAppName,
                        tags = screenshot.tags,
                        isFavorite = screenshot.isFavorite,
                        dateStr = dateStr
                    )
                }
                bulkInsertScreenshotUseCase(uiScreenshots)
                Timber.i("Successfully saved all ${screenshotsToSave.size} screenshots")
            }.onSuccess {
                updateState { copy(showCompletionMessage = true) }
            }.onFailure { exception ->
                Timber.e(exception, "Failed to save screenshots")
                // TODO: Show error message to user
            }.also {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun getAvailableTags(): List<String> {
        return listOf(
            "쇼핑", "직무 관련", "레퍼런스"
        )
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
    fun getCurrentScreenshotTags(): List<String> {
        val state = currentState
        return when (state.organizeMode) {
            OrganizeMode.BATCH -> {
                // 한번에 모드: 모든 스크린샷이 공통으로 가진 태그들만 표시
                if (state.screenshots.isEmpty()) {
                    emptyList()
                } else {
                    // 모든 스크린샷이 공통으로 가지고 있는 태그들만 선택된 상태로 표시
                    val allTags = state.screenshots.flatMap { it.tags }.distinct()
                    val commonTags = allTags.filter { tag ->
                        state.screenshots.all { screenshot -> screenshot.tags.contains(tag) }
                    }
                    Timber.d("Batch mode - Common tags across all screenshots: $commonTags")
                    commonTags
                }
            }
            OrganizeMode.SINGLE -> {
                // 한장씩 모드: 현재 인덱스의 스크린샷 태그
                val currentTags =
                    state.screenshots.getOrNull(state.currentIndex)?.tags ?: emptyList()
                Timber.d("Single mode - Current screenshot tags: $currentTags")
                currentTags
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

    /**
     * 파일명에서 앱 이름을 파싱하는 함수
     * 예: "Screenshot_20231215_143022_Instagram.jpg" -> "Instagram"
     * 예: "Screenshot_Instagram_20231215.png" -> "Instagram"
     * 예: "스크린샷_2023-12-15_14-30-22_카카오톡.jpg" -> "카카오톡"
     */
    private fun parseAppNameFromFileName(fileName: String?): String {
        if (fileName.isNullOrBlank()) {
            Timber.w("Filename is null or blank, returning empty app name")
            return ""
        }

        return runCatching {
            // 파일 확장자 제거
            val nameWithoutExtension = fileName.substringBeforeLast(".")
            Timber.d("Processing filename without extension: $nameWithoutExtension")

            // 다양한 패턴으로 앱 이름 추출 시도
            val appName = when {
                // 패턴 1: Screenshot_날짜_시간_앱이름
                nameWithoutExtension.contains("Screenshot_") && nameWithoutExtension.count { it == '_' } >= 3 -> {
                    val parts = nameWithoutExtension.split("_")
                    if (parts.size >= 4) parts.drop(3).joinToString("_") else ""
                }

                // 패턴 2: Screenshot_앱이름_날짜 또는 Screenshot_앱이름
                nameWithoutExtension.startsWith("Screenshot_") -> {
                    val withoutPrefix = nameWithoutExtension.removePrefix("Screenshot_")
                    // 날짜 패턴 제거 (YYYYMMDD, YYYY-MM-DD 등)
                    withoutPrefix.split("_")
                        .firstOrNull { part ->
                            !part.matches(Regex("\\d{8}")) && // YYYYMMDD
                                    !part.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) && // YYYY-MM-DD
                                    !part.matches(Regex("\\d{6}")) && // HHMMSS
                                    !part.matches(Regex("\\d{2}-\\d{2}-\\d{2}")) // HH-MM-SS
                        } ?: ""
                }

                // 패턴 3: 스크린샷_날짜_시간_앱이름 (한글)
                nameWithoutExtension.contains("스크린샷_") -> {
                    val parts = nameWithoutExtension.split("_")
                    // 마지막 부분이 앱 이름일 가능성이 높음
                    parts.lastOrNull { part ->
                        !part.contains("스크린샷") &&
                                !part.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                                !part.matches(Regex("\\d{2}-\\d{2}-\\d{2}")) &&
                                !part.matches(Regex("\\d+"))
                    } ?: ""
                }

                // 패턴 4: 기타 언더스코어로 구분된 경우, 마지막 단어가 앱 이름일 가능성
                nameWithoutExtension.contains("_") -> {
                    val parts = nameWithoutExtension.split("_")
                    parts.lastOrNull { part ->
                        !part.matches(Regex("\\d+")) && // 숫자만 있는 부분 제외
                                part.length > 1 // 한 글자 제외
                    } ?: ""
                }

                // 패턴이 매칭되지 않으면 빈 문자열
                else -> ""
            }

            // 결과 정리
            val cleanedAppName = appName.trim()
            Timber.d("Extracted app name: '$cleanedAppName'")
            cleanedAppName

        }.onFailure { exception ->
            Timber.w(exception, "Failed to parse app name from filename: $fileName")
        }.getOrDefault("")
    }
}