package com.prography.home.ui.storage.viewmodel

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.prography.home.ui.storage.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val app: Application,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<ScreenshotState, ScreenshotEffect, ScreenshotAction>(
    initialState = ScreenshotState()
) {
    private val dateFormat = SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREA)

    init {
        loadScreenshots()
    }

    fun refreshScreenshots() {
        loadScreenshots()
    }

    private fun loadScreenshots() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = mutableListOf<ScreenshotItem>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf("Screenshots")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor = app.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val nameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val dateSeconds = it.getLong(dateCol)
                    val fileName = it.getString(nameCol)

                    val uriItem = ContentUris.withAppendedId(uri, id)
                    val dateStr = dateFormat.format(Date(dateSeconds * 1000))

                    items.add(
                        ScreenshotItem(
                            id = id.toString(),
                            uri = uriItem,
                            dateGroup = dateStr,
                            isSelected = false,
                            fileName = fileName
                        )
                    )
                }
            } ?: Timber.d("ScreenshotViewModel", "Cursor is null - no access to media store")

            val grouped = items.groupBy { it.dateGroup }
            Timber.d("ScreenshotViewModel", "Loading completed: ${items.size} screenshots found")

            updateState {
                copy(
                    groupedScreenshots = grouped,
                    totalCount = items.size,
                    selectedCount = 0,
                    isSelectionMode = false,
                    isAllSelected = false
                )
            }
        }
    }

    override fun handleAction(action: ScreenshotAction) {
        when (action) {
            is ScreenshotAction.ToggleSelect -> {
                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.map {
                        if (it.id == action.id) it.copy(isSelected = !it.isSelected) else it
                    }
                }
                val flat = updated.values.flatten()
                val count = flat.count { it.isSelected }
                if (count > 20) {
                    showToast("최대 20장까지 선택할 수 있어요.")
                    return // 더 이상 처리하지 않음
                }
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = count,
                        isSelectionMode = count > 0,
                        isAllSelected = count == totalCount
                    )
                }
            }

            ScreenshotAction.ShowDeleteDialog -> {
                updateState { copy(showDeleteDialog = true) }
            }

            ScreenshotAction.SelectAll -> {
                val totalCount = currentState.totalCount
                if (totalCount > 20) {
                    showToast("최대 20장까지 선택할 수 있어요.")
                    return // 초과 시 선택 중단
                }
                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.map { it.copy(isSelected = true) }
                }
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = totalCount,
                        isSelectionMode = true,
                        isAllSelected = true
                    )
                }
            }

            ScreenshotAction.CancelSelection -> {
                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.map { it.copy(isSelected = false) }
                }
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false
                    )
                }
            }

            ScreenshotAction.DeleteSelected -> {
                updateState { copy(showDeleteDialog = true) }
            }

            ScreenshotAction.ConfirmDelete -> {
                val selectedItems = currentState.groupedScreenshots.values.flatten().filter { it.isSelected }
                val deletedIds = selectedItems.map { it.id }

                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.filterNot { it.id in deletedIds }
                }

                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false,
                        showDeleteDialog = false
                    )
                }
            }


            ScreenshotAction.DismissDeleteDialog -> {
                updateState { copy(showDeleteDialog = false) }
            }

            ScreenshotAction.OrganizeSelected -> {
                // 선택된 스크린샷들을 정리하기 화면으로 전달
                val selectedItems = currentState.groupedScreenshots.values.flatten()
                    .filter { it.isSelected }

                if (selectedItems.isEmpty()) {
                    showToast("정리할 스크린샷을 선택해주세요")
                    return@handleAction
                }

                val selectedIds = selectedItems.map { it.id }

                // 정리하기 화면으로 이동 (ID만 전달)
                navigationHelper.navigate(
                    NavigationEvent.To(AppRoute.Organize(screenshotIds = selectedIds))
                )
            }

            ScreenshotAction.OrganizeCompleted -> {
                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.map { it.copy(isSelected = false) }
                }
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = 0,
                        isSelectionMode = false,
                        isAllSelected = false
                    )
                }
            }

            ScreenshotAction.RefreshScreenshots -> {
                loadScreenshots()
            }
        }
    }
}
