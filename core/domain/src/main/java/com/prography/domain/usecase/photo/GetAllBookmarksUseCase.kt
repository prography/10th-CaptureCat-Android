package com.prography.domain.usecase.photo

import com.prography.domain.model.UiPhotoHomeModel
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRemoteRepository
import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetAllBookmarksUseCase @Inject constructor(
    private val localRepo: PhotoLocalRepository,
    private val remoteRepo: PhotoRemoteRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<UiPhotoHomeModel>> {
        return userPreferenceRepository.accessToken
            .flatMapLatest { token ->
                if (!token.isNullOrEmpty()) {
                    // 로그인(서버) : 추후에 변경
                    localRepo.getAllBookmarks()
                } else {
                    // 비로그인(로컬)
                    localRepo.getAllBookmarks()
                }
            }
    }
}