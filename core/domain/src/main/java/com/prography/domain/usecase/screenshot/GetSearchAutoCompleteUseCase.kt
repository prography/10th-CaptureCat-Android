package com.prography.domain.usecase.screenshot

import com.prography.domain.model.AutocompleteTagModel
import com.prography.domain.model.TagModel
import com.prography.domain.repository.ScreenshotRepository
import javax.inject.Inject

class GetSearchAutoCompleteUseCase @Inject constructor(
    private val screenshotRepository: ScreenshotRepository
) {
    suspend operator fun invoke(keyword: String, size: Int = 10): List<TagModel> {
        return screenshotRepository.getSearchAutoComplete(keyword, size)
    }
}