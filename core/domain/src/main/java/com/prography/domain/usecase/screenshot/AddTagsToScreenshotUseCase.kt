package com.prography.domain.usecase.screenshot

import com.prography.domain.model.TagModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class AddTagsToScreenshotUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(
        screenshotId: String,
        tagNames: List<String>
    ): Result<List<TagModel>> {
        return repository.addTagsToScreenshot(screenshotId, tagNames)
    }
}
