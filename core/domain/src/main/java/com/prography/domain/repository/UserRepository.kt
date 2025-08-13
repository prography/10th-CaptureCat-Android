package com.prography.domain.repository

import com.prography.domain.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(): Result<UserInfo>
}
