package com.prography.home.ui.storage.source

import android.Manifest
import android.app.Application
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.paging.PagingSource
import androidx.paging.PagingState
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
            // 권한 체크
            val permission = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_IMAGES
                else -> Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ContextCompat.checkSelfPermission(
                    app,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.w("Permission not granted: $permission")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

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
            // LIMIT과 OFFSET 제거 - 전체 데이터를 가져온 후 페이징 처리
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor =
                app.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val nameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                val allItems = mutableListOf<ScreenshotItem>()
                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val dateSeconds = it.getLong(dateCol)
                    val fileName = it.getString(nameCol)

                    val uriItem = ContentUris.withAppendedId(uri, id)
                    val dateStr = dateFormat.format(Date(dateSeconds * 1000))

                    allItems.add(
                        ScreenshotItem(
                            id = id.toString(),
                            uri = uriItem,
                            dateGroup = dateStr,
                            isSelected = false,
                            fileName = fileName
                        )
                    )
                }

                // 페이징 처리
                val startIndex = page * pageSize
                val endIndex = minOf(startIndex + pageSize, allItems.size)

                if (startIndex < allItems.size) {
                    items.addAll(allItems.subList(startIndex, endIndex))
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