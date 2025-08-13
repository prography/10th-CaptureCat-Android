package com.prography.data.repository

import com.prography.domain.model.UserInfo
import com.prography.domain.repository.UserRepository
import com.prography.network.api.UserService
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    override suspend fun getUserInfo(): Result<UserInfo> {
        return runCatching {
            val response = userService.getUserInfo()
            if (!response.isSuccessful) throw IllegalStateException("Network error: ${response.code()}")
            val body = response.body() ?: throw IllegalStateException("Empty body")
            val data = body.data ?: throw IllegalStateException("No data")
            UserInfo(
                email = data.email,
                nickname = data.nickname,
                tutorialCompleted = data.tutorialCompleted
            )
        }
    }
}
