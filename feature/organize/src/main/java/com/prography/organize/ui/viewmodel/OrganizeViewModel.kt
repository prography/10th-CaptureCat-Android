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
import android.provider.MediaStore
import com.prography.util.MixpanelUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.prography.domain.usecase.storage.SaveOrganizedIdsUseCase
import com.prography.domain.usecase.tag.GetUserTagsUseCase
import com.prography.domain.usecase.user.GetDeletePromptSettingUseCase
import java.io.IOException

@HiltViewModel
class OrganizeViewModel @Inject constructor(
    private val bulkInsertScreenshotUseCase: BulkInsertScreenshotUseCase,
    private val getUserTagsUseCase: GetUserTagsUseCase,
    private val addRecentTagUseCase: AddRecentTagUseCase,
    private val saveOrganizedIdsUseCase: SaveOrganizedIdsUseCase,
    private val getDeletePromptSettingUseCase: GetDeletePromptSettingUseCase,
    @ApplicationContext private val context: Context
) : BaseComposeViewModel<OrganizeState, OrganizeEffect, OrganizeAction>(
    initialState = OrganizeState()
) {
    private val _entryPoint = MutableStateFlow("inbox")
    val entryPoint: StateFlow<String> = _entryPoint.asStateFlow()

    fun setEntryPoint(point: String) {
        _entryPoint.value = point
    }

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

            OrganizeAction.OnSystemDeleteFinished -> {
                // 시스템 알럿이 종료되면 결과 무관하게 완료 네비게이션
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
                // 1) 스샷들에서 태그 제거
                val updatedScreenshots = when (organizeMode) {
                    OrganizeMode.BATCH -> {
                        screenshots.map { sc ->
                            val updatedTags = sc.tags.filterNot { it.name.equals(tagText, ignoreCase = true) }
                            sc.copy(tags = updatedTags)
                        }
                    }
                    OrganizeMode.SINGLE -> {
                        screenshots.map { sc ->
                            if (sc.id == screenshotId) {
                                val updatedTags = sc.tags.filterNot { it.name.equals(tagText, ignoreCase = true) }
                                sc.copy(tags = updatedTags)
                            } else sc
                        }
                    }
                }

                // 2) 해당 태그가 더 이상 어떤 스샷에서도 안 쓰이면 availableTags에서도 제거
                val tagStillUsed = updatedScreenshots.any { sc ->
                    sc.tags.any { it.name.equals(tagText, ignoreCase = true) }
                }
                val updatedAvailable =
                    if (tagStillUsed) availableTags
                    else availableTags.filterNot { it.name.equals(tagText, ignoreCase = true) }

                copy(
                    screenshots = updatedScreenshots,
                    availableTags = updatedAvailable
                )
            }
        } else if (currentTags.size >= 4) {
            showToast("태그는 최대 4개까지 지정할 수 있어요.")
        } else {
            val newTagModel = TagModel(System.currentTimeMillis(), tagText)
            updateState {
                val updatedScreenshots = when (organizeMode) {
                    OrganizeMode.BATCH -> {
                        screenshots.map { sc ->
                            if (sc.tags.any { it.name.equals(tagText, ignoreCase = true) }) sc
                            else sc.copy(tags = sc.tags + newTagModel)
                        }
                    }
                    OrganizeMode.SINGLE -> {
                        screenshots.map { sc ->
                            if (sc.id == screenshotId && sc.tags.none { it.name.equals(tagText, ignoreCase = true) }) {
                                sc.copy(tags = sc.tags + newTagModel)
                            } else sc
                        }
                    }
                }

                // 추가 시 availableTags 맨 앞에(또는 치환) 반영 – 기존 로직 유지/강화
                val updatedAvailable =
                    if (availableTags.any { it.name.equals(newTagModel.name, ignoreCase = true) }) {
                        availableTags.map { if (it.name.equals(newTagModel.name, ignoreCase = true)) newTagModel else it }
                            .distinctBy { it.name.lowercase() }
                    } else listOf(newTagModel) + availableTags

                copy(
                    screenshots = updatedScreenshots,
                    availableTags = updatedAvailable
                )
            }
        }
    }



    private fun canAttach(sc: OrganizeScreenshotItem, name: String): Boolean =
        sc.tags.size < 4 && sc.tags.none { it.name.equals(name, ignoreCase = true) }

    private fun addNewTagToScreenshot(screenshotId: String, tagText: String) {
        val t = tagText.trim()
        if (t.isEmpty()) { showToast("태그를 입력해 주세요."); return }

        viewModelScope.launch {
            showLoading()
            runCatching {
                // 서버/레포 성공 시 TagModel 반환 (Flow<TagModel>의 첫 값)
                addRecentTagUseCase(t).first()
            }.onSuccess { saved ->
                updateState {
                    val newAvailable = run {
                        val idx = availableTags.indexOfFirst { it.name.equals(saved.name, ignoreCase = true) }
                        if (idx >= 0) {
                            availableTags.toMutableList().apply { set(idx, saved) }.toList()
                        } else {
                            availableTags + saved
                        }
                    }

                    // 2) 스크린샷들에 실제 태그 부착 (mode에 따라 대상 달라짐)
                    val newScreenshots = when (organizeMode) {
                        OrganizeMode.BATCH -> {
                            screenshots.map { sc ->
                                if (canAttach(sc, saved.name)) sc.copy(tags = sc.tags + saved) else sc
                            }
                        }
                        OrganizeMode.SINGLE -> {
                            screenshots.map { sc ->
                                if (sc.id == screenshotId && canAttach(sc, saved.name))
                                    sc.copy(tags = sc.tags + saved)
                                else sc
                            }
                        }
                    }

                    copy(availableTags = newAvailable, screenshots = newScreenshots)
                }

                // 필요 시 트래킹
                // MixpanelUtil.track("create_tag", mapOf("tag" to saved.name))

            }.onFailure { e ->
                Timber.e(e, "Failed to persist recent tag")
                showToast("태그 저장 실패: ${e.message ?: ""}")
            }
            hideLoading()
        }
    }


    private fun saveScreenshots() {
        val screenshotsToSave = currentState.screenshots
        viewModelScope.launch {
            showLoading()
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
                        isBookmarked = screenshot.isFavorite,
                        dateStr = dateStr
                    )
                }
                bulkInsertScreenshotUseCase(uiScreenshots)
            }.onSuccess {
                // 정리 완료된 스샷 ID 저장 (기존 로직 유지)
                runCatching { saveOrganizedIdsUseCase(screenshotsToSave.map { it.id }) }
                    .onFailure { Timber.e(it, "Failed to persist organized ids") }

                val uniqueTags = screenshotsToSave.flatMap { it.tags }.distinctBy { it.name }
                MixpanelUtil.track(
                    "click_save_image",
                    mapOf(
                        "entry_point" to entryPoint.value,
                        "tagging_mode" to if (uiState.value.organizeMode == OrganizeMode.BATCH) "batch" else "single",
                        "tag_count_total" to uniqueTags.size,
                        "screenshot_count" to uiState.value.screenshots.size,
                    )
                )

                hideLoading()

                // ✅ 설정 확인 후 시스템 삭제 알럿 유도
                val promptEnabled = getDeletePromptSettingUseCase().getOrElse { false }

                Timber.d("promptEnabled: $promptEnabled")
                if (promptEnabled) {
                    // 현재 화면에 보이는 스크린샷들(정리한 것들)을 갤러리에서 삭제 요청
                    val uris = screenshotsToSave.mapNotNull { it.uri as? Uri ?: runCatching { Uri.parse(it.uri.toString()) }.getOrNull() }
                    emitEffect(OrganizeEffect.RequestSystemDelete(uris))
                } else {
                    // 기존 동작
                    emitEffect(OrganizeEffect.NavigateToComplete)
                }

            }.onFailure {
                hideLoading()
                showToast("스크린샷 업로드에 실패했습니다.")
            }
        }
    }

    private fun getAvailableTags(): List<TagModel> {
        return listOf(
            TagModel(0, "쇼핑"),
            TagModel(0,"직무 관련"),
            TagModel(0,"레퍼런스")
        )
    }


    private fun loadRecentTags() {
        viewModelScope.launch {
            try {
                getUserTagsUseCase().fold(
                    onSuccess = { tags ->
                        updateState { copy(availableTags = tags) }
                    },
                    onFailure = { e ->
                        val errorMessage = when (e) {
                            is IOException -> "네트워크 연결 오류입니다. 다시 시도해 주세요."
                            else -> "태그를 불러오는데 실패했습니다: ${e.message}"
                        }
                        updateState { copy(availableTags = getAvailableTags()) }
                        showToast(errorMessage)
                    }
                )
            } catch (e: Exception) {
                val errorMessage = "태그를 불러오는데 실패했습니다: ${e.message}"
                updateState { copy(availableTags = getAvailableTags()) }
                showToast(errorMessage)
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