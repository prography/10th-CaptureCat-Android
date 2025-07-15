package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class BulkInsertScreenshotUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshots: List<UiScreenshotModel>) {
        return repository.bulkInsert(screenshots)
    }
}