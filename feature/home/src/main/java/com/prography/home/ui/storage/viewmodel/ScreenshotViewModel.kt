package com.prography.home.ui.storage.viewmodel

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prography.domain.usecase.auth.CheckLoginStatusUseCase
import com.prography.domain.usecase.auth.GetAuthTokenUseCase
import com.prography.domain.usecase.storage.GetOrganizedIdsUseCase
import com.prography.domain.usecase.storage.SaveOrganizedIdsUseCase
import com.prography.home.ui.storage.contract.*
import com.prography.home.ui.storage.source.ScreenshotOrganizePagingSource
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import com.prography.util.MixpanelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val app: Application,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val navigationHelper: NavigationHelper,
    private val getOrganizedIdsUseCase: GetOrganizedIdsUseCase,
    private val saveOrganizedIdsUseCase: SaveOrganizedIdsUseCase
) : BaseComposeViewModel<ScreenshotState, ScreenshotEffect, ScreenshotAction>(
    initialState = ScreenshotState()
) {
    private val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA)

    // Paging3 Flow 추가
    val screenshotsPagingFlow: Flow<PagingData<ScreenshotItem>> =
        Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { ScreenshotOrganizePagingSource(app) }
        ).flow.cachedIn(viewModelScope)

    init {
        loadInitialData()
    }

    fun refreshScreenshots() {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            // 로컬 저장된 organized ids 불러오기
            val stored = runCatching { getOrganizedIdsUseCase() }.getOrDefault(emptySet())
            updateState {
                copy(
                    currentPage = 0,
                    hasMoreData = true,
                    isLoggedIn = checkLoginStatusUseCase(),
                    organizedScreenshotIds = stored,
                    refreshVersion = refreshVersion + 1
                )
            }
        }
    }

    private fun getAllScreenshotIdsFromMediaStore(): List<String> {
        val result = mutableListOf<String>()
        try {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf("Screenshots")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor = app.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    result.add(id.toString())
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching all screenshot IDs")
        }
        return result
    }


    override fun handleAction(action: ScreenshotAction) {
        when (action) {
            is ScreenshotAction.ToggleSelect -> {
                // 선택된 아이템 관리는 별도로 처리
                val currentSelected = currentState.selectedItems
                val updatedSelected = if (currentSelected.contains(action.id)) {
                    currentSelected - action.id
                } else {
                    if (currentSelected.size >= 20) {
                        showToast("최대 20장까지 선택할 수 있어요.")
                        return
                    }
                    currentSelected + action.id
                }

                updateState {
                    copy(
                        selectedItems = updatedSelected,
                        selectedCount = updatedSelected.size,
                        isSelectionMode = updatedSelected.isNotEmpty(),
                        isAllSelected = false // 개별 선택이므로 전체선택은 해제
                    )
                }
            }

            ScreenshotAction.ShowDeleteDialog -> {
                updateState { copy(showDeleteDialog = true) }
            }

            is ScreenshotAction.SelectAll -> {
                viewModelScope.launch(Dispatchers.IO) {
                    // MediaStore에서 실제 모든 스크린샷 ID 읽기
                    val allIds = getAllScreenshotIdsFromMediaStore()

                    withContext(Dispatchers.Main) {
                        val selectedIds = allIds.takeIf { it.isNotEmpty() } ?: emptyList()
                        updateState {
                            copy(
                                selectedItems = selectedIds.toSet(),
                                selectedCount = selectedIds.size,
                                isSelectionMode = selectedIds.isNotEmpty(),
                                isAllSelected = true
                            )
                        }
                        showToast("총 ${selectedIds.size}장의 스크린샷이 선택되었습니다.")
                    }
                }
            }


            ScreenshotAction.CancelSelection -> {
                updateState {
                    copy(
                        selectedItems = emptySet(),
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false
                    )
                }
            }

            ScreenshotAction.Back -> {
                navigationHelper.navigate(NavigationEvent.Up)
            }
            ScreenshotAction.DeleteSelected -> {
                MixpanelUtil.track("image_delete_click")
                updateState { copy(showDeleteDialog = true) }
            }

            ScreenshotAction.ConfirmDelete -> {
                // 삭제 후 선택 초기화
                updateState {
                    copy(
                        selectedItems = emptySet(),
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false,
                        showDeleteDialog = false
                    )
                }
            }

            ScreenshotAction.GoToNotice -> {
                navigationHelper.navigate(NavigationEvent.To(AppRoute.SettingRoute.Notice))
            }


            ScreenshotAction.DismissDeleteDialog -> {
                updateState { copy(showDeleteDialog = false) }
            }

            ScreenshotAction.OrganizeSelected -> {
                MixpanelUtil.track("image_upload_click")
                val selectedIds = currentState.selectedItems.toList()
                if (selectedIds.isEmpty()) {
                    showToast("정리할 스크린샷을 선택해주세요")
                    return
                }

                navigationHelper.navigate(
                    NavigationEvent.To(
                        AppRoute.Organize(
                            screenshotIds = selectedIds,
                            entryPoint = "inbox"
                        )
                    )
                )
            }

            ScreenshotAction.OrganizeCompleted -> {
                updateState {
                    copy(
                        selectedItems = emptySet(),
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false
                    )
                }
            }
            ScreenshotAction.RefreshScreenshots -> {
                refreshScreenshots()
            }

            ScreenshotAction.NavigateToLogin -> {
                navigationHelper.navigate(NavigationEvent.To(AppRoute.Login))
            }

            ScreenshotAction.LoadMoreScreenshots -> {
                // Paging3가 자동으로 처리하므로 빈 구현
            }
        }
    }
}
