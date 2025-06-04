package com.prography.data.local.datasource


import com.prography.data.local.entity.PhotoLocalEntity
import kotlinx.coroutines.flow.Flow

interface PhotoLocalDataSource {
    suspend fun insertBookmark(photo: PhotoLocalEntity)
    fun getAllBookmarks(): Flow<List<PhotoLocalEntity>>
    suspend fun deleteBookmark(photoId: String)
    suspend fun clearBookmarks()
    suspend fun isPhotoBookmarked(photoId: String): Boolean
}