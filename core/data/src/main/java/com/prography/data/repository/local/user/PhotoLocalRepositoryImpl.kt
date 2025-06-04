package com.prography.data.repository.local.user

import com.prography.data.local.datasource.PhotoLocalDataSource
import com.prography.data.mapper.PhotoLocalMapper.toDomain
import com.prography.data.mapper.PhotoLocalMapper.toEntity
import com.prography.domain.model.BookmarkPhoto
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoLocalRepositoryImpl @Inject constructor(
    private val localDataSource: PhotoLocalDataSource
) : PhotoLocalRepository {
    override suspend fun insertBookmark(photo: BookmarkPhoto) =
        localDataSource.insertBookmark(photo.toEntity())

    override fun getAllBookmarks(): Flow<List<BookmarkPhoto>> {
        return localDataSource.getAllBookmarks()
            .map {
                it.map { photoEntity -> photoEntity.toDomain() }
            }
    }

    override suspend fun deleteBookmark(photoId: String) =
        localDataSource.deleteBookmark(photoId)

    override suspend fun clearBookmarks() = localDataSource.clearBookmarks()

    override suspend fun isPhotoBookmarked(photoId: String): Boolean =
        localDataSource.isPhotoBookmarked(photoId)
}
