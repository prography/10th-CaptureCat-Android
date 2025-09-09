package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class DeleteTagsUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(tagIds: List<Int>) {
        return screenshotRepository.deleteTags(tagIds)
    }

    suspend fun deleteSingleTag(tagId: Int) {
        return screenshotRepository.deleteTag(tagId)
    }

    suspend fun deleteAllTags() {
        return screenshotRepository.deleteAllTags()
    }
}