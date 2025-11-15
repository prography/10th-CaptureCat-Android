package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteAllDataUseCase @Inject constructor(
    private val deleteAllScreenshots: DeleteAllScreenshotUseCase,
    private val deleteAllTags: DeleteAllTagsUseCase,
) {
    /** 둘 다 성공하면 Success, 하나라도 실패하면 Failure */
    suspend operator fun invoke(): Result<Unit> = runCatching {
        coroutineScope {
            awaitAll(
                async { deleteAllScreenshots() },
                async { deleteAllTags() }
            )
        }
    }
}