package com.prography.home.ui.home.upload

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.BulkInsertScreenshotUseCase
import com.prography.domain.usecase.screenshot.DeleteAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetAllLocalScreenshotsUseCase
import com.prography.domain.usecase.tag.AddUserTagUseCase
import com.prography.domain.usecase.tag.GetLocalUserTagsUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

data class UploadState(
    val uploading: Boolean = false,
    val uploadedCount: Int = 0,
    val totalCount: Int = 0,
    val showConfirmDialog: Boolean = false,
    val error: String? = null,
    val completed: Boolean = false
)

sealed class UploadAction {
    object CancelUpload : UploadAction() // 업로드 중단
    object ConfirmCancelUpload : UploadAction() // 팝업 "그만두기" 선택
    object DismissCancelDialog : UploadAction()
    object Finish : UploadAction() // "다음" 클릭
}

sealed class UploadEffect {
    object NavigateToUploaded : UploadEffect() // 업로드 완료 화면
}

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val bulkInsertScreenshotUseCase: BulkInsertScreenshotUseCase,
    private val getAllLocalScreenshotsUseCase: GetAllLocalScreenshotsUseCase,
    private val deleteAllScreenshotsUseCase: DeleteAllScreenshotsUseCase,
    private val addUserTagUseCase: AddUserTagUseCase,
    private val getLocalUserTagsUseCase: GetLocalUserTagsUseCase,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<UploadState, UploadEffect, UploadAction>(
    initialState = UploadState()
) {

    private var isCanceled = false

    init {
        startUpload()
    }

    override fun handleAction(action: UploadAction) {
        when (action) {
            is UploadAction.CancelUpload -> updateState { copy(showConfirmDialog = true) }
            is UploadAction.ConfirmCancelUpload -> {
                isCanceled = true
                navigationHelper.navigate(NavigationEvent.To(AppRoute.Main()))
            }

            is UploadAction.DismissCancelDialog -> updateState { copy(showConfirmDialog = false) }
            is UploadAction.Finish -> navigationHelper.navigate(NavigationEvent.To(AppRoute.Main()))
        }
    }

    private fun startUpload() {
        viewModelScope.launch {
            getAllLocalScreenshotsUseCase().collect { screenshots ->
                updateState { copy(uploading = true, totalCount = screenshots.size, uploadedCount = 0, completed = false) }
                isCanceled = false
                var successCount = 0

                for (s in screenshots) {
                    if (isCanceled) break
                    try {
                        bulkInsertScreenshotUseCase(listOf(s))
                        successCount++
                        updateState { copy(uploadedCount = successCount) }
                    } catch (e: Exception) {
                        updateState { copy(error = "이미지 업로드 실패: ${e.message}") }
                        showToast("일부 이미지 업로드 실패")
                    }
                }

                if (!isCanceled && successCount == screenshots.size) {
                    try {
                        // 1) 로컬 이미지 정리
                        deleteAllScreenshotsUseCase()

                        // 2) 유저 태그 로컬 스냅샷 → 서버에 반영
                        val userTagModels = getLocalUserTagsUseCase().first()
                        val tagNames = userTagModels.mapNotNull { it.name?.trim() }.filter { it.isNotEmpty() }.distinct()

                        if (tagNames.isNotEmpty()) {
                            addUserTagUseCase(tagNames)
                                .catch { t ->
                                    Timber.e(t, "Failed to sync user tags")
                                    showToast("태그 동기화에 실패했습니다")
                                }
                                .collect { saved ->
                                    Timber.d("Saved TagModels: $saved")
                                    MixpanelUtil.track("sync_user_tags_after_upload",
                                        mapOf("count" to tagNames.size, "tags" to tagNames))
                                }
                        }

                        updateState { copy(uploading = false, completed = true) }
                        emitEffect(UploadEffect.NavigateToUploaded)
                    } catch (e: Exception) {
                        updateState { copy(error = "로컬 삭제/태그 동기화 실패: ${e.message}") }
                        showToast("마무리 작업 중 오류가 발생했습니다")
                    }
                } else if (!isCanceled) {
                    updateState { copy(uploading = false) }
                }
            }
        }
    }
}
