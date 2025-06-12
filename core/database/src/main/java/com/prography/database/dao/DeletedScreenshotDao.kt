package com.prography.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prography.database.model.DeletedScreenshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedScreenshotDao {

    @Query("SELECT * FROM deleted_screenshots")
    fun getAllDeletedScreenshots(): Flow<List<DeletedScreenshotEntity>>

    @Query("SELECT fileName FROM deleted_screenshots")
    suspend fun getDeletedFileNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedScreenshots(screenshots: List<DeletedScreenshotEntity>)

    @Query("DELETE FROM deleted_screenshots WHERE fileName IN (:fileNames)")
    suspend fun removeDeletedScreenshots(fileNames: List<String>)

    @Query("DELETE FROM deleted_screenshots")
    suspend fun clearAllDeletedScreenshots()
}