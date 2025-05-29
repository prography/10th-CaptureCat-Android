package com.prography.domain.model

data class UiPhotoModel(
    val photoList: List<PhotoModel>
)

data class PhotoModel(
    val id: String,
    val imageUrls: ImageUrls // JSON의 `urls` 키에서 값 가져오기
)

data class ImageUrls(
    val small: String,  // RecyclerView에 사용할 작은 이미지 URL
    val regular: String // Glide로 로딩할 이미지 URL
)
