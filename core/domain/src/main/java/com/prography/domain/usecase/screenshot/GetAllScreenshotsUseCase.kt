package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllScreenshotsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(hasTags: Boolean? = null): Flow<List<UiScreenshotModel>> {
        return repository.getScreenshots(hasTags = hasTags)
    }

    // 페이징용 새 메서드
    suspend fun getScreenshots(
        page: Int = 0,
        pageSize: Int = 20,
        hasTags: Boolean? = null
    ): List<UiScreenshotModel> {
        return repository.getScreenshots(page = page, pageSize = pageSize, hasTags = hasTags)
    }
}
