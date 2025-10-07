package com.prography.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String
)