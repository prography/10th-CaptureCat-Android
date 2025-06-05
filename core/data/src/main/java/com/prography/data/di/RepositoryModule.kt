package com.prography.data.di

import com.prography.database.datasource.PhotoLocalDataSource
import com.prography.data.local.repository.PhotoLocalRepositoryImpl
import com.prography.data.local.repository.UserPreferenceRepositoryImpl
import com.prography.data.remote.repository.PhotoRepositoryImpl
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRepository
import com.prography.domain.repository.UserPreferenceRepository
import com.prography.network.datasource.PhotoRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePhotoRepository(
        photoRemoteDataSourceImpl: PhotoRemoteDataSourceImpl
    ) : PhotoRepository {
        return PhotoRepositoryImpl(
            photoRemoteDataSourceImpl
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(
        userPreferenceDataStore: UserPreferenceDataStore
    ): UserPreferenceRepository = UserPreferenceRepositoryImpl(userPreferenceDataStore)

    @Provides
    @Singleton
    fun providePhotoLocalRepository(
        photoLocalDataSource: PhotoLocalDataSource
    ): PhotoLocalRepository = PhotoLocalRepositoryImpl(photoLocalDataSource)
}