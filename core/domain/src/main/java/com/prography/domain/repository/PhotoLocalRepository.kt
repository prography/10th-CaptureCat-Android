package com.prography.domain.repository

import com.prography.domain.model.BookmarkPhoto
import kotlinx.coroutines.flow.Flow

interface PhotoLocalRepository {
    suspend fun insertBookmark(photo: BookmarkPhoto)
    fun getAllBookmarks(): Flow<List<BookmarkPhoto>>
    suspend fun deleteBookmark(photoId: String)
    suspend fun clearBookmarks()
    suspend fun isPhotoBookmarked(photoId: String): Boolean
}