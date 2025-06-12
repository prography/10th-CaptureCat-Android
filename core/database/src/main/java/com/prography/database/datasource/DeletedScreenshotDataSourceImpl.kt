package com.prography.database.datasource

import com.prography.database.dao.DeletedScreenshotDao
import com.prography.database.model.DeletedScreenshotEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletedScreenshotDataSourceImpl @Inject constructor(
    private val deletedScreenshotDao: DeletedScreenshotDao
) : DeletedScreenshotDataSource {

    override fun getAllDeletedScreenshots(): Flow<List<DeletedScreenshotEntity>> {
        return deletedScreenshotDao.getAllDeletedScreenshots()
    }

    override suspend fun getDeletedFileNames(): List<String> {
        return deletedScreenshotDao.getDeletedFileNames()
    }

    override suspend fun insertDeletedScreenshots(screenshots: List<DeletedScreenshotEntity>) {
        deletedScreenshotDao.insertDeletedScreenshots(screenshots)
    }

    override suspend fun removeDeletedScreenshots(fileNames: List<String>) {
        deletedScreenshotDao.removeDeletedScreenshots(fileNames)
    }

    override suspend fun clearAllDeletedScreenshots() {
        deletedScreenshotDao.clearAllDeletedScreenshots()
    }
}