package com.prography.data.di

import com.prography.data.local.repository.UserPreferenceRepositoryImpl
import com.prography.data.repository.AuthRepositoryImpl
import com.prography.data.repository.OrganizedRepositoryImpl
import com.prography.domain.repository.AuthRepository
import com.prography.domain.repository.OrganizedRepository
import com.prography.domain.repository.UserPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindOrganizedRepository(
        impl: OrganizedRepositoryImpl
    ): OrganizedRepository
}
