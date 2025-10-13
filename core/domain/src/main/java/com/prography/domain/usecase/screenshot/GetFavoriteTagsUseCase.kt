package com.prography.domain.usecase.screenshot

import com.prography.domain.model.TagWithCount
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetFavoriteTagsUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke(size: Int): List<TagWithCount> {
        return repository.getFavoriteTags(size)
    }
}