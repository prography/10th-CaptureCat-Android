package com.android.start

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// State
data class StartChooseState(
    val screenshots: List<ScreenshotItem> = emptyList(),
    val selectedScreenshots: List<ScreenshotItem> = emptyList(),
    val isLoading: Boolean = false
)

// Action
sealed class StartChooseAction {
    data class ToggleSelection(val item: ScreenshotItem, val maxSelectable: Int) : StartChooseAction()
    object LoadScreenshots : StartChooseAction()
}

@HiltViewModel
class StartChooseViewModel @Inject constructor(
    private val app: Application
) : BaseComposeViewModel<StartChooseState, Nothing, StartChooseAction>(
    initialState = StartChooseState()
) {

    init {
        sendAction(StartChooseAction.LoadScreenshots)
    }

    override fun handleAction(action: StartChooseAction) {
        when (action) {
            is StartChooseAction.ToggleSelection -> toggleSelection(
                action.item,
                action.maxSelectable
            )

            is StartChooseAction.LoadScreenshots -> loadScreenshots()
        }
    }

    /**
     * 선택된 스크린샷 추가/삭제.
     */
    private fun toggleSelection(item: ScreenshotItem, maxSelectable: Int) {
        val currentSelected = currentState.selectedScreenshots

        if (currentSelected.contains(item)) {
            updateState {
                copy(selectedScreenshots = selectedScreenshots - item)
            }
        } else if (currentSelected.size < maxSelectable) {
            updateState {
                copy(selectedScreenshots = selectedScreenshots + item)
            }
        } else {
            showToast("최대 ${maxSelectable}개까지 선택 가능합니다.")
        }
    }

    /**
     * 기기에서 실제 스크린샷 이미지를 로드합니다.
     */
    private fun loadScreenshots() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH, // 더 정확한 위치 필터링
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val items = mutableListOf<ScreenshotItem>()

            val cursor = app.contentResolver.query(uri, projection, null, null, sortOrder)
            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val pathCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val relativePath = it.getString(pathCol)

                    // 경로에 "Screenshots"가 포함된 경우만 필터링 (더 범용적)
                    if (!relativePath.contains("Screenshots", ignoreCase = true)) continue

                    val imageUri = ContentUris.withAppendedId(uri, id)

                    items.add(
                        ScreenshotItem(
                            id = id.toString(),
                            uri = imageUri.toString()
                        )
                    )
                }
            }

            // 메인 스레드에서 상태 업데이트
            updateState {
                copy(
                    screenshots = items,
                    isLoading = false
                )
            }
        }
    }
}
