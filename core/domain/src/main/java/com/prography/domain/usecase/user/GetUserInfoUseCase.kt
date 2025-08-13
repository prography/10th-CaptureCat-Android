package com.prography.domain.usecase.user

import com.prography.domain.model.UserInfo
import com.prography.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserInfo> = userRepository.getUserInfo()
}