package com.prography.domain.usecase.photo

import com.prography.domain.model.UiPhotoHomeModel
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBookmarksUseCase @Inject constructor(
    private val repo: PhotoLocalRepository
) {
    operator fun invoke(): Flow<List<UiPhotoHomeModel>> = repo.getAllBookmarks()
}