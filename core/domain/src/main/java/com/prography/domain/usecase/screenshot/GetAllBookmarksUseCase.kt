package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllBookmarksUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(): Flow<List<UiScreenshotModel>> {
        return repository.getScreenshots()
            .map { list -> list.filter { it.isFavorite } }
    }
}
