package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class UpdateScreenshotUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshot: UiScreenshotModel) {
        screenshotRepository.update(screenshot)
    }
}