package com.prography.home.ui.home.upload

import androidx.lifecycle.viewModelScope
import com.prography.domain.usecase.screenshot.BulkInsertScreenshotUseCase
import com.prography.domain.usecase.screenshot.DeleteAllScreenshotsUseCase
import com.prography.domain.usecase.screenshot.GetAllLocalScreenshotsUseCase
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

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
                updateState {
                    copy(
                        uploading = true,
                        totalCount = screenshots.size,
                        uploadedCount = 0,
                        completed = false
                    )
                }
                isCanceled = false
                var successCount = 0
                for (screenshot in screenshots) {
                    if (isCanceled) break
                    try {
                        bulkInsertScreenshotUseCase(listOf(screenshot))
                        successCount++
                        updateState { copy(uploadedCount = successCount) }
                    } catch (e: Exception) {
                        updateState { copy(error = "이미지 업로드 실패: ${e.message}") }
                        showToast("일부 이미지 업로드 실패")
                    }
                }
                if (!isCanceled && successCount == screenshots.size) {
                    // 모두 업로드 성공 → 로컬 이미지 데이터 삭제
                    try {
                        deleteAllScreenshotsUseCase()
                        updateState { copy(uploading = false, completed = true) }
                        emitEffect(UploadEffect.NavigateToUploaded)
                    } catch (e: Exception) {
                        updateState { copy(error = "로컬 삭제 실패: ${e.message}") }
                        showToast("로컬 이미지 삭제에 실패했습니다")
                    }
                } else if (!isCanceled) {
                    // 일부 실패해도 navigate 할지, 에러만 띄울지는 정책에 따라 분기
                    updateState { copy(uploading = false) }
                }
            }
        }
    }
}
