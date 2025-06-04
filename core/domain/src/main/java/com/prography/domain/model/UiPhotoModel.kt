package com.prography.domain.model

data class UiPhotoModel(
    val photoList: List<PhotoModel>
)

data class PhotoModel(
    val id: String,
    val imageUrls: String
)