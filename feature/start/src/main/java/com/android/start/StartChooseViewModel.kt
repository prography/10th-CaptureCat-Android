package com.android.start

import android.app.Application
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prography.ui.BaseComposeViewModel
import com.prography.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// State
data class StartChooseState(
    val selectedScreenshots: List<ScreenshotItem> = emptyList(),
    val totalCount: Int = 0 // 전체 스크린샷 개수
)

// Action
sealed class StartChooseAction {
    data class ToggleSelection(val screenshot: ScreenshotItem, val maxSelectable: Int) :
        StartChooseAction()
}

@HiltViewModel
class StartChooseViewModel @Inject constructor(
    private val app: Application
) : BaseComposeViewModel<StartChooseState, Nothing, StartChooseAction>(
    initialState = StartChooseState()
) {

    val screenshotsPagingFlow: Flow<PagingData<ScreenshotItem>> =
        Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { StartChoosePagingSource(app) }
        ).flow.cachedIn(viewModelScope)

    init {
        loadTotalCount()
    }

    override fun handleAction(action: StartChooseAction) {
        when (action) {
            is StartChooseAction.ToggleSelection -> toggleSelection(
                action.screenshot,
                action.maxSelectable
            )
        }
    }

    /**
     * 전체 스크린샷 개수만 미리 가져옴
     */
    private fun loadTotalCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf("Screenshots")

            val cursor = app.contentResolver.query(uri, projection, selection, selectionArgs, null)
            val count = cursor?.count ?: 0
            cursor?.close()

            updateState { copy(totalCount = count) }
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
            showToast(app.getString(R.string.error_max_screenshots_selection, maxSelectable))
        }
    }
}
