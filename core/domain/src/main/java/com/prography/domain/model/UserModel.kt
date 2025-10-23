package com.prography.domain.model

data class LoginResult(
    val email: String,
    val nickname: String,
    val tutorialCompleted: Boolean,
    // 계정 연동 관련 필드들
    val isEmailAlreadyRegistered: Boolean = false,
    val existingProvider: String? = null,
    val linkToken: String? = null
)