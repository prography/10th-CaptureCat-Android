package com.prography.data.local.repository

import com.prography.database.datasource.PhotoLocalDataSource
import com.prography.data.mapper.PhotoLocalMapper.toDomain
import com.prography.data.mapper.PhotoLocalMapper.toEntity
import com.prography.domain.model.UiPhotoHomeModel
import com.prography.domain.repository.PhotoLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoLocalRepositoryImpl @Inject constructor(
    private val localDataSource: PhotoLocalDataSource
) : PhotoLocalRepository {
    override suspend fun insertBookmark(photo: UiPhotoHomeModel) =
        localDataSource.insertBookmark(photo.toEntity())

    override fun getAllBookmarks(): Flow<List<UiPhotoHomeModel>> {
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
