package com.prography.domain.usecase.screenshot

import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class SearchImagesByTagsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 20
    ): List<UiScreenshotModel> {
        return repository.searchImagesByTags(tagNames, page, size)
    }
}