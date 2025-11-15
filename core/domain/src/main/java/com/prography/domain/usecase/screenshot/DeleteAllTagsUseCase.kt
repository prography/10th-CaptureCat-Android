package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import com.prography.domain.repository.TagRepository
import javax.inject.Inject

class DeleteAllTagsUseCase @Inject constructor(
    private val repository: TagRepository
) {
    suspend operator fun invoke() = repository.clearRecentTags()
}
