package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetFavoriteImagesUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 10): Result<List<UiScreenshotModel>> {
        return screenshotRepository.getFavoriteImages(page, size)
    }
}