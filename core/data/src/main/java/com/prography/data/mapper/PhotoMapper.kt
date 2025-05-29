package com.prography.data.mapper

import com.prography.data.entity.PhotoResponse
import com.prography.domain.model.ImageUrls
import com.prography.domain.model.PhotoModel
import com.prography.domain.model.UiPhotoModel

fun List<PhotoResponse>.toUiPhotoResponseModel(): UiPhotoModel {
    val photoResponse = this.map {
        PhotoModel(
            id = it.id,
            imageUrls = ImageUrls(
                it.imageUrls.small,
                it.imageUrls.regular
            )
        )
    }
    return UiPhotoModel(
        photoList = photoResponse
    )
}