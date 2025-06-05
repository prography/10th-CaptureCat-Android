package com.prography.domain.repository

import com.prography.domain.model.UiPhotoHomeModel
import kotlinx.coroutines.flow.Flow

interface PhotoLocalRepository {
    suspend fun insertBookmark(photo: UiPhotoHomeModel)
    fun getAllBookmarks(): Flow<List<UiPhotoHomeModel>>
    suspend fun deleteBookmark(photoId: String)
    suspend fun clearBookmarks()
    suspend fun isPhotoBookmarked(photoId: String): Boolean
}