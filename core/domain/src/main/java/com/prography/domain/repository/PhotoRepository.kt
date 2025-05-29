package com.prography.domain.repository

import com.prography.domain.model.UiPhotoModel

interface PhotoRepository{
    suspend fun getRandomPhotos(accessKey: String, countIdx : Int): Result<UiPhotoModel>
}