package com.prography.domain.usecase.screenshot

import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class DeleteAllScreenshotUseCase @Inject constructor(
    private val repository: ScreenshotRepository
) {
    suspend operator fun invoke() = repository.deleteAll()
}
