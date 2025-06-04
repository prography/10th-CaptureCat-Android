package com.prography.data.local.datasource


import com.prography.data.local.entity.BookmarkPhoto
import kotlinx.coroutines.flow.Flow

interface PhotoLocalDataSource {
    suspend fun insertBookmark(photo: BookmarkPhoto)
    fun getAllBookmarks(): Flow<List<BookmarkPhoto>>
    suspend fun deleteBookmark(photoId: String)
    suspend fun clearBookmarks()
    suspend fun isPhotoBookmarked(photoId: String): Boolean
}