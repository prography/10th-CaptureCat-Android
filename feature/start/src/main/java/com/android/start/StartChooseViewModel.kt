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
     * 초기 데이터를 추가.
     */
    fun loadMockScreenshots() {
        screenshots.clear()
        screenshots.addAll(
            listOf(
                ScreenshotItem("1", "https://via.placeholder.com/150"),
                ScreenshotItem("2", "https://via.placeholder.com/150"),
                ScreenshotItem("3", "https://via.placeholder.com/150"),
                ScreenshotItem("4", "https://via.placeholder.com/150"),
                ScreenshotItem("5", "https://via.placeholder.com/150"),
                ScreenshotItem("6", "https://via.placeholder.com/150"),
                ScreenshotItem("7", "https://via.placeholder.com/150"),
                ScreenshotItem("8", "https://via.placeholder.com/150"),
                ScreenshotItem("9", "https://via.placeholder.com/150")
            )
        )
    }

    /**
     * 기기에서 실제 스크린샷 이미지를 로드합니다.
     */
    fun loadScreenshots() {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf("Screenshots")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val items = mutableListOf<ScreenshotItem>()

            val cursor = app.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val fileName = it.getString(nameCol)
                    val imageUri = ContentUris.withAppendedId(uri, id)

                    items.add(
                        ScreenshotItem(
                            id = id.toString(),
                            uri = imageUri.toString(), // URI를 문자열로 저장
                        )
                    )
                }
            }

            // 데이터를 UI에 반영
            screenshots.clear()
            screenshots.addAll(items)
        }
    }
}
