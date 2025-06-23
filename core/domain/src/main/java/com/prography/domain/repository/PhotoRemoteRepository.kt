package com.prography.domain.repository

import com.prography.domain.model.UiPhotoModel

interface PhotoRemoteRepository{
    suspend fun getRandomPhotos(accessKey: String, countIdx : Int): Result<UiPhotoModel>
}