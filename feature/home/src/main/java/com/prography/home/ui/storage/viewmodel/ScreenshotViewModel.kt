package com.prography.home.ui.storage.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.prography.domain.repository.DeletedScreenshotRepository
import com.prography.home.ui.storage.contract.*
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.navigation.OrganizeDataCache
import com.prography.ui.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val app: Application,
    private val deletedScreenshotRepository: DeletedScreenshotRepository,
    private val navigationHelper: NavigationHelper
) : BaseComposeViewModel<ScreenshotState, ScreenshotEffect, ScreenshotAction>(
    initialState = ScreenshotState()
) {
    private val dateFormat = SimpleDateFormat("yyyy년 M월 d일 (E)", Locale.KOREA)

    init {
        loadScreenshots()
    }

    private fun loadScreenshots() {
        viewModelScope.launch(Dispatchers.IO) {
            val deletedFileNames = deletedScreenshotRepository.getDeletedFileNames().toSet()
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

                    // 삭제된 파일은 제외
                    if (fileName in deletedFileNames) continue

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
            }

            val grouped = items.groupBy { it.dateGroup }
            updateState {
                copy(
                    groupedScreenshots = grouped,
                    totalCount = items.size,
                    selectedCount = 0,
                    isAllSelected = false,
                    isSelectionMode = false
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
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = count,
                        isSelectionMode = count > 0,
                        isAllSelected = count == totalCount
                    )
                }
            }

            ScreenshotAction.SelectAll -> {
                val updated = currentState.groupedScreenshots.mapValues { (_, list) ->
                    list.map { it.copy(isSelected = true) }
                }
                val all = updated.values.flatten()
                updateState {
                    copy(
                        groupedScreenshots = updated,
                        selectedCount = all.size,
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
                viewModelScope.launch(Dispatchers.IO) {
                    val selectedItems = currentState.groupedScreenshots.values.flatten()
                        .filter { it.isSelected }

                    val fileNames = selectedItems.mapNotNull { it.fileName }

                    if (fileNames.isNotEmpty()) {
                        deletedScreenshotRepository.addDeletedScreenshots(fileNames)
                        emitEffect(ScreenshotEffect.ShowDeleteToast)
                    }

                    // 다이얼로그 닫고 선택 해제
                    updateState {
                        copy(
                            showDeleteDialog = false,
                            selectedCount = 0,
                            isSelectionMode = false,
                            isAllSelected = false
                        )
                    }

                    // 스크린샷 목록 다시 로드
                    loadScreenshots()
                }
            }

            ScreenshotAction.DismissDeleteDialog -> {
                updateState { copy(showDeleteDialog = false) }
            }

            ScreenshotAction.OrganizeSelected -> {
                // 선택된 스크린샷들을 정리하기 화면으로 전달
                val selectedItems = currentState.groupedScreenshots.values.flatten()
                    .filter { it.isSelected }

                if (selectedItems.isNotEmpty()) {
                    // OrganizeDataCache에 선택된 스크린샷 데이터를 저장
                    val cacheData = selectedItems.map { item ->
                        OrganizeDataCache.ScreenshotData(
                            id = item.id,
                            uri = item.uri,
                            fileName = item.fileName
                        )
                    }
                    OrganizeDataCache.setScreenshots(cacheData)

                    // 정리하기 화면으로 이동 (선택 상태는 유지)
                    navigationHelper.navigate(NavigationEvent.To(AppRoute.Organize))
                }
            }

            ScreenshotAction.OrganizeCompleted -> {
                // 정리하기 완료 시 선택 상태 초기화
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
        }
    }
}
