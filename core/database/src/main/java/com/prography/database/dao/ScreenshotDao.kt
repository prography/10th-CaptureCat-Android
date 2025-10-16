package com.prography.database.dao

import androidx.room.*
import com.prography.database.model.ScreenshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotDao {

    @Query("SELECT * FROM screenshots")
    fun getAll(): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE id = :id")
    suspend fun getById(id: String): ScreenshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(screenshot: ScreenshotEntity)

    @Update
    suspend fun update(screenshot: ScreenshotEntity)

    @Delete
    suspend fun delete(screenshot: ScreenshotEntity)

    @Query("DELETE FROM screenshots WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM screenshots")
    suspend fun deleteAll()

    @Query("SELECT * FROM screenshots WHERE isBookmarked = 1")
    fun getBookmarked(): Flow<List<ScreenshotEntity>>
}
