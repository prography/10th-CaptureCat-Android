package com.prography.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val email: String,
    val nickname: String,
    val tutorialCompleted: Boolean
)
