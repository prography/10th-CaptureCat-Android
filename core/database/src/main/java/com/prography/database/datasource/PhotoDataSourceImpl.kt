package com.prography.database.datasource

import com.prography.data.local.datasource.PhotoLocalDataSource
import com.prography.database.dao.BookmarkPhotoDao
import com.prography.database.model.BookmarkPhoto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PhotoDataSourceImpl @Inject constructor(
    private val bookmarkPhotoDao: BookmarkPhotoDao
) : PhotoLocalDataSource {

    override suspend fun insertBookmark(photo: BookmarkPhoto) {
        bookmarkPhotoDao.insertBookmark(photo)
    }

    override fun getAllBookmarks(): Flow<List<BookmarkPhoto>> {
        return bookmarkPhotoDao.getAllBookmarks()
    }

    override suspend fun deleteBookmark(photoId: String) {
        bookmarkPhotoDao.deleteBookmark(photoId)
    }

    override suspend fun clearBookmarks() {
        bookmarkPhotoDao.clearBookmarks()
    }

    override suspend fun isPhotoBookmarked(photoId: String): Boolean {
        return bookmarkPhotoDao.isPhotoBookmarked(photoId) > 0
    }
}