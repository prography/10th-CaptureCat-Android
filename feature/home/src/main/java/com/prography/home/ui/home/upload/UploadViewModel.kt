package com.prography.home.ui.home.upload

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.BulkInsertScreenshotUseCase
import com.prography.domain.usecase.screenshot.DeleteAllDataUseCase
import com.prography.domain.usecase.screenshot.GetAllLocalScreenshotsUseCase
import com.prography.domain.usecase.tag.AddUserTagUseCase
import com.prography.domain.usecase.tag.GetLocalUserTagsUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.ui.R
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
    private val app: Application,
    private val bulkInsertScreenshotUseCase: BulkInsertScreenshotUseCase,
    private val getAllLocalScreenshotsUseCase: GetAllLocalScreenshotsUseCase,
    private val deleteAllDataUseCase: DeleteAllDataUseCase,
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
                navigationHelper.navigate(NavigationEvent.To(AppRoute.Main(), popUpTo = true))
            }

            is UploadAction.DismissCancelDialog -> updateState { copy(showConfirmDialog = false) }
            is UploadAction.Finish -> navigationHelper.navigate(NavigationEvent.To(AppRoute.Main(), popUpTo = true))
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
                        Timber.e(e, "Screenshot upload failed: ${e.message}")
                        updateState { copy(error = app.getString(R.string.error_image_upload_failed, e.message)) }
                        showToast(app.getString(R.string.error_partial_upload_failed))
                        // 에러 발생 시 루프 중단
                        break
                    }
                }

                // 업로드된 스크린샷이 하나라도 있으면 태그 동기화 진행
                if (!isCanceled && successCount > 0) {
                    try {
                        // 1) 유저 로컬 태그 리스트 가져오기
                        val userTagModels = getLocalUserTagsUseCase().first()
                        val tagNames = userTagModels.mapNotNull { it.name?.trim() }.filter { it.isNotEmpty() }.distinct()

                        // 2) 유저 로컬 태그 리스트 → 서버에 반영
                        var tagSyncSuccess = true
                        if (tagNames.isNotEmpty()) {
                            try {
                                addUserTagUseCase(tagNames).collect { saved ->
                                    Timber.d("Saved TagModels: $saved")
                                    MixpanelUtil.track("sync_user_tags_after_upload",
                                        mapOf("count" to tagNames.size, "tags" to tagNames))
                                }
                            } catch (tagError: Exception) {
                                Timber.e(tagError, "Failed to sync user tags")
                                tagSyncSuccess = false
                                // 태그 동기화 실패 시 경고만 표시하고 계속 진행
                                showToast(app.getString(R.string.error_finish_work_failed))
                            }
                        }

                        // 3. 모든 스크린샷이 성공적으로 업로드되고 태그 동기화도 성공한 경우에만 로컬 데이터 삭제
                        if (successCount == screenshots.size && tagSyncSuccess) {
                            deleteAllDataUseCase()
                            Timber.d("Local data deleted successfully")
                        } else {
                            Timber.w("Partial upload: $successCount/${screenshots.size}, keeping local data")
                        }

                        updateState { copy(uploading = false, completed = true) }
                        emitEffect(UploadEffect.NavigateToUploaded)
                    } catch (e: Exception) {
                        Timber.e(e, "Error during tag sync or cleanup")
                        updateState { copy(error = app.getString(R.string.error_sync_failed, e.message)) }
                        showToast(app.getString(R.string.error_finish_work_failed))
                        updateState { copy(uploading = false) }
                        emitEffect(UploadEffect.NavigateToUploaded)
                    }
                } else if (!isCanceled) {
                    // 업로드 실패 시
                    updateState { copy(uploading = false) }
                }
            }
        }
    }
}
