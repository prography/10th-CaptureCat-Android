package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllScreenshotsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(): Flow<List<UiScreenshotModel>> {
        return repository.getScreenshots()
    }
}
