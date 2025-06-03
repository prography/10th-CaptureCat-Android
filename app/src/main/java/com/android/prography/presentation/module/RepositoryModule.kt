package com.android.prography.presentation.module

import com.prography.data.remote.datasource.PhotoRemoteDataSourceImpl
import com.prography.data.repository.local.user.UserPreferenceRepositoryImpl
import com.prography.data.repository.remote.photo.PhotoRepositoryImpl
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.PhotoRepository
import com.prography.domain.repository.UserPreferenceRepository
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
}