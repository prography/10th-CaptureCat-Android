package com.prography.domain.usecase.screenshot

import com.prography.domain.model.TagWithCount
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetMostUsedTagsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(size: Int = 5): List<TagWithCount> {
        return repository.getMostUsedTags(size)
    }
}