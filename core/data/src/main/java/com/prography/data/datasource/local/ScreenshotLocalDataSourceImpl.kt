package com.prography.data.datasource.local

import com.prography.data.mapper.toUiScreenshotModel
import com.prography.data.mapper.toUiScreenshotModels
import com.prography.data.mapper.toDomain
import com.prography.data.mapper.toEntity
import com.prography.database.dao.ScreenshotDao
import com.prography.domain.model.UiScreenshotModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
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

    override suspend fun deleteTag(imageId: String, tagName: String) {
        // 전체 스크린샷을 가져와서 해당 ID의 태그를 삭제
        val screenshots = dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

        // 이 구현은 실시간 업데이트가 아니므로 나중에 개선 필요
        // 현재는 ViewModel에서 UpdateScreenshotUseCase를 사용하는 것이 더 적합
        Timber.d("deleteTag called for imageId: $imageId, tagName: $tagName")
        throw UnsupportedOperationException("Use UpdateScreenshotUseCase instead for tag deletion")
    }

    override suspend fun getById(screenshotId: String): UiScreenshotModel? {
        return dao.getById(screenshotId)?.toDomain()
    }
}
