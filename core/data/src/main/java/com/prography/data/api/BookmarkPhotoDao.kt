package com.prography.data.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prography.data.entity.BookmarkPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(photo: BookmarkPhoto)

    @Query("SELECT * FROM bookmark_photos")
    fun getAllBookmarks(): Flow<List<BookmarkPhoto>>

    // 특정 북마크 삭제
    @Query("DELETE FROM bookmark_photos WHERE id = :photoId")
    suspend fun deleteBookmark(photoId: String)

    // 모든 북마크 삭제
    @Query("DELETE FROM bookmark_photos")
    suspend fun clearBookmarks()

    // 특정 북마크 존재 여부 확인
    @Query("SELECT COUNT(*) FROM bookmark_photos WHERE id = :photoId")
    suspend fun isPhotoBookmarked(photoId: String): Int
}
