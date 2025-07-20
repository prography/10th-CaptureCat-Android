package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshotId: String, isBookmarked: Boolean) {
        screenshotRepository.toggleBookmark(screenshotId, isBookmarked)
    }
}