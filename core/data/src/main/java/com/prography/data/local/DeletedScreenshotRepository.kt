package com.prography.data.local

import com.prography.database.datasource.DeletedScreenshotDataSource
import com.prography.database.model.DeletedScreenshotEntity
import com.prography.domain.repository.DeletedScreenshotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletedScreenshotRepositoryImpl @Inject constructor(
    private val deletedScreenshotDataSource: DeletedScreenshotDataSource
) : DeletedScreenshotRepository {

    fun getAllDeletedScreenshots(): Flow<List<DeletedScreenshotEntity>> {
        return deletedScreenshotDataSource.getAllDeletedScreenshots()
    }

    override suspend fun getDeletedFileNames(): List<String> {
        return deletedScreenshotDataSource.getDeletedFileNames()
    }

    override suspend fun addDeletedScreenshots(fileNames: List<String>) {
        val entities = fileNames.map { fileName ->
            DeletedScreenshotEntity(fileName = fileName)
        }
        deletedScreenshotDataSource.insertDeletedScreenshots(entities)
    }

    override suspend fun removeDeletedScreenshots(fileNames: List<String>) {
        deletedScreenshotDataSource.removeDeletedScreenshots(fileNames)
    }

    override suspend fun clearAllDeletedScreenshots() {
        deletedScreenshotDataSource.clearAllDeletedScreenshots()
    }
}
