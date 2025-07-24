package com.android.start

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import timber.log.Timber

class StartChoosePagingSource(
    private val app: Application
) : PagingSource<Int, ScreenshotItem>() {

    // 전체 스크린샷을 캐시하여 페이징 처리
    private var allScreenshots: List<ScreenshotItem>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ScreenshotItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = 20

            Timber.d("StartChoosePagingSource: page=$page, pageSize=$pageSize")

            // 첫 번째 로드시에만 전체 데이터를 가져옴
            if (allScreenshots == null) {
                allScreenshots = loadAllScreenshots()
            }

            val screenshots = allScreenshots ?: emptyList()
            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, screenshots.size)

            val items = if (startIndex < screenshots.size) {
                screenshots.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

            Timber.d("StartChoosePagingSource loaded: ${items.size} items (total: ${screenshots.size})")

            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (endIndex >= screenshots.size) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "StartChoosePagingSource error")
            LoadResult.Error(e)
        }
    }

    private fun loadAllScreenshots(): List<ScreenshotItem> {
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
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val uriItem = ContentUris.withAppendedId(uri, id)
                items.add(ScreenshotItem(id = id.toString(), uri = uriItem.toString()))
            }
        }

        Timber.d("StartChoosePagingSource loadAllScreenshots: ${items.size} total items")
        return items
    }

    override fun getRefreshKey(state: PagingState<Int, ScreenshotItem>): Int? = 0
}