package com.prography.data.di

import com.prography.database.datasource.DeletedScreenshotDataSource
import com.prography.database.datasource.PhotoLocalDataSource
import com.prography.data.local.repository.DeletedScreenshotRepositoryImpl
import com.prography.data.local.repository.PhotoLocalRepositoryImpl
import com.prography.data.local.repository.UserPreferenceRepositoryImpl
import com.prography.data.remote.repository.PhotoRemoteRepositoryImpl
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.DeletedScreenshotRepository
import com.prography.domain.repository.PhotoLocalRepository
import com.prography.domain.repository.PhotoRemoteRepository
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
    ) : PhotoRemoteRepository {
        return PhotoRemoteRepositoryImpl(
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

    @Provides
    @Singleton
    fun provideDeletedScreenshotRepository(
        deletedScreenshotDataSource: DeletedScreenshotDataSource
    ): DeletedScreenshotRepository = DeletedScreenshotRepositoryImpl(deletedScreenshotDataSource)
}
