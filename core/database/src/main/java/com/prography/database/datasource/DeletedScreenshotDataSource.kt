package com.prography.database.datasource

import com.prography.database.model.DeletedScreenshotEntity
import kotlinx.coroutines.flow.Flow

interface DeletedScreenshotDataSource {
    fun getAllDeletedScreenshots(): Flow<List<DeletedScreenshotEntity>>
    suspend fun getDeletedFileNames(): List<String>
    suspend fun insertDeletedScreenshots(screenshots: List<DeletedScreenshotEntity>)
    suspend fun removeDeletedScreenshots(fileNames: List<String>)
    suspend fun clearAllDeletedScreenshots()
}