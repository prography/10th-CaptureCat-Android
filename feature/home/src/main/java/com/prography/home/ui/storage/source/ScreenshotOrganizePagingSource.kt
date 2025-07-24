package com.prography.home.ui.storage.source

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prography.domain.model.UiScreenshotModel
import com.prography.home.ui.storage.contract.ScreenshotItem
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ScreenshotOrganizePagingSource(
    private val app: Application
) : PagingSource<Int, ScreenshotItem>() {

    private val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ScreenshotItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = 20 // 고정된 페이지 크기
            
            Timber.d("LocalPagingSource: page=$page, pageSize=$pageSize")

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
            val sortOrder =
                "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $pageSize OFFSET ${page * pageSize}"

            val cursor =
                app.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
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
            }

            Timber.d("LocalPagingSource loaded: ${items.size} items")

            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (items.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e, "LocalPagingSource error")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ScreenshotItem>): Int? = 0
}