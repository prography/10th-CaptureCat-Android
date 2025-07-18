package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(imageId: String, tagName: String) {
        screenshotRepository.deleteTag(imageId, tagName)
    }
}