package com.prography.data.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PhotoResponse(
    val id: String,
    @SerialName("urls") val imageUrls: ImageUrls // JSON의 `urls` 키에서 값 가져오기
)

@Serializable
data class ImageUrls(
    val small: String,  // RecyclerView에 사용할 작은 이미지 URL
    val regular: String // Glide로 로딩할 이미지 URL
)