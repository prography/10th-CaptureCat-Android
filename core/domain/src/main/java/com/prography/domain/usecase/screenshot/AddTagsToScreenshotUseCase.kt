package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class AddTagsToScreenshotUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshotId: String, tagNames: List<String>) {
        return repository.addTagsToScreenshot(screenshotId, tagNames)
    }
}
