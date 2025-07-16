package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshResponse(
    val result: String
)