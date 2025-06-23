package com.android.prography.presentation.module

import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRemoteRepository
import com.prography.domain.repository.UserPreferenceRepository
import com.prography.domain.usecase.photo.GetAllBookmarksUseCase
import com.prography.domain.usecase.photo.GetRandomImageUseCase
import com.prography.domain.usecase.user.GetOnboardingShownUseCase
import com.prography.domain.usecase.user.SetOnboardingShownUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetOnboardingShownUseCase(userPreferenceRepository: UserPreferenceRepository) =
        GetOnboardingShownUseCase(userPreferenceRepository)

    @Provides
    @Singleton
    fun provideSetOnboardingShownUseCase(userPreferenceRepository: UserPreferenceRepository) =
        SetOnboardingShownUseCase(userPreferenceRepository)
}