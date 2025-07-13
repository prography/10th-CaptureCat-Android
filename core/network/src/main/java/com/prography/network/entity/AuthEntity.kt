package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginRequest(
    val idToken: String
)

@Serializable
data class SocialLoginResponse(
    val result: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)