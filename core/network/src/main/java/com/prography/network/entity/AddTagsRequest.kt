package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class AddTagsRequest(
    val tagNames: List<String>
)