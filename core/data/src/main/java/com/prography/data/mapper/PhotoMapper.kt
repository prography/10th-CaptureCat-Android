package com.prography.data.mapper

import com.prography.network.entity.PhotoResponse
import com.prography.domain.model.PhotoModel
import com.prography.domain.model.UiPhotoModel

fun List<PhotoResponse>.toUiPhotoResponseModel(): UiPhotoModel {
    val photoResponse = this.map {
        PhotoModel(
            id = it.id,
            imageUrls = it.imageUrls
        )
    }
    return UiPhotoModel(
        photoList = photoResponse
    )
}