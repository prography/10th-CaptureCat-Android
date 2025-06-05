package com.prography.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PhotoResponse(
    val id: String,
    val imageUrls: String
)
