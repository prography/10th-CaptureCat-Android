package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    val id: Int,
    val name: String,
    val url: String,
    val captureDate: String,
    val tags: List<TagResponse>
)

