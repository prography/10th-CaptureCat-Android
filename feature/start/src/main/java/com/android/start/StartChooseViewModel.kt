package com.android.start

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartChooseViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    // 스크린샷 리스트를 관리
    val screenshots = mutableStateListOf<ScreenshotItem>()

    // 선택된 스크린샷 리스트
    val selectedScreenshots = mutableStateListOf<ScreenshotItem>()

    init {
        loadScreenshots()
    }

    /**
     * 선택된 스크린샷 추가/삭제.
     */
    fun toggleSelection(item: ScreenshotItem, maxSelectable: Int) {
        if (selectedScreenshots.contains(item)) {
            selectedScreenshots.remove(item)
        } else if (selectedScreenshots.size < maxSelectable) {
            selectedScreenshots.add(item)
        }
    }

    /**
     * 기기에서 실제 스크린샷 이미지를 로드합니다.
     */
    fun loadScreenshots() {
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

            // 메인 스레드에서 리스트 업데이트
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                screenshots.clear()
                screenshots.addAll(items)
            }
        }
    }
}
