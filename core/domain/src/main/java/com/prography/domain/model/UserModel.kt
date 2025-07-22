package com.prography.domain.model

data class LoginResult(
    val email: String,
    val nickname: String,
    val tutorialCompleted: Boolean
)