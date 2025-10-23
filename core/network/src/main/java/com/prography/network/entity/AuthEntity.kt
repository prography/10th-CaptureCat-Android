package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginRequest(
    val idToken: String,
    val authToken: String? = null,
    val accountLinking : Boolean = false,
    val linkToken : String? = null
)

@Serializable
data class SocialLoginResponse(
    val email: String,
    val nickname: String,
    val tutorialCompleted: Boolean,
    val provider: String? = "",
    val existingProvider: String? = null, // 기존 계정의 제공자 정보
    val linkToken: String? = null
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)