package com.prography.domain.usecase.user
import com.prography.domain.model.LoginProvider
import com.prography.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentLoginProviderUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) { operator fun invoke(): Flow<LoginProvider> = repo.recentLoginProvider }

class SetRecentLoginProviderUseCase @Inject constructor(
    private val repo: UserPreferenceRepository
) { suspend operator fun invoke(provider: LoginProvider) = repo.setRecentLoginProvider(provider) }