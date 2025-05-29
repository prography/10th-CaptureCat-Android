package com.prography.domain.usecase


import com.prography.domain.model.UiPhotoModel
import com.prography.domain.repository.PhotoRepository
import javax.inject.Inject

class GetRandomImageUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
){
    suspend operator fun invoke(
        accessKey: String,
        countIdx : Int
    ) : Result<UiPhotoModel>{
        return photoRepository.getRandomPhotos(accessKey, countIdx)
    }
}