package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetScreenshotByIdUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshotId: String): UiScreenshotModel? {
        return repository.getScreenshotById(screenshotId)
    }
}