package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class InsertScreenshotUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(screenshot: UiScreenshotModel) {
        repository.insert(screenshot)
    }
}
