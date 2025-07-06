package com.prography.data.datasource.local

import com.prography.data.mapper.toDomain
import com.prography.data.mapper.toEntity
import com.prography.database.dao.ScreenshotDao
import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ScreenshotLocalDataSourceImpl @Inject constructor(
    private val dao: ScreenshotDao
) : ScreenshotLocalDataSource {

    override suspend fun getScreenshots(): Flow<List<UiScreenshotModel>> {
        return dao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insert(screenshot: UiScreenshotModel) {
        dao.insert(screenshot.toEntity())
    }

    override suspend fun update(screenshot: UiScreenshotModel) {
        dao.update(screenshot.toEntity())
    }

    override suspend fun delete(screenshot: UiScreenshotModel) {
        dao.delete(screenshot.toEntity())
    }

    override suspend fun deleteById(screenshotId: String) {
        dao.deleteById(screenshotId)
    }
}
