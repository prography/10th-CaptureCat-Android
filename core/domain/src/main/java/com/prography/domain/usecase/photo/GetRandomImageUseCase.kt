package com.prography.domain.usecase.photo


import com.prography.domain.model.UiPhotoModel
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRemoteRepository
import javax.inject.Inject

class GetRandomImageUseCase @Inject constructor(
    private val photoRemoteRepository: PhotoRemoteRepository,
    private val repo: PhotoLocalRepository
){
    suspend operator fun invoke(
        accessKey: String,
        countIdx : Int
    ) : Result<UiPhotoModel>{
        return photoRemoteRepository.getRandomPhotos(accessKey, countIdx)
    }
}