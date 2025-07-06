package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class DeleteScreenshotUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshotId: String) {
        screenshotRepository.deleteScreenshot(screenshotId)
    }
}