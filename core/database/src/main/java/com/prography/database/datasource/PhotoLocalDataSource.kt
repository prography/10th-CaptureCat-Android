package com.prography.database.datasource


import com.prography.database.model.PhotoLocalModel
import kotlinx.coroutines.flow.Flow

interface PhotoLocalDataSource {
    suspend fun insertBookmark(photo: PhotoLocalModel)
    fun getAllBookmarks(): Flow<List<PhotoLocalModel>>
    suspend fun deleteBookmark(photoId: String)
    suspend fun clearBookmarks()
    suspend fun isPhotoBookmarked(photoId: String): Boolean
}