package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTagRequest(
    val currentTagId: Long,
    val newTagName: String
)