package com.prography.database.datasource

import com.prography.data.local.datasource.PhotoLocalDataSource
import com.prography.data.local.entity.PhotoLocalEntity
import com.prography.database.dao.BookmarkPhotoDao
import com.prography.database.mapper.PhotoMapper.toEntity
import com.prography.database.mapper.PhotoMapper.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PhotoLocalDataSourceImpl @Inject constructor(
    private val bookmarkPhotoDao: BookmarkPhotoDao
) : PhotoLocalDataSource {

    override suspend fun insertBookmark(photo: PhotoLocalEntity) {
        bookmarkPhotoDao.insertBookmark(photo.toModel())
    }

    override fun getAllBookmarks(): Flow<List<PhotoLocalEntity>> {

        return bookmarkPhotoDao.getAllBookmarks()
            .map {
                it.map { photoLocal -> photoLocal.toEntity() }
            }
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