package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetRelatedTagsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(
        tagNames: List<String>,
        page: Int = 0,
        size: Int = 10
    ): List<String> {
        return repository.getRelatedTags(tagNames, page, size)
    }
}