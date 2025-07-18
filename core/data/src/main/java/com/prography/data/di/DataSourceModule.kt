package com.prography.data.di

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.local.ScreenshotLocalDataSourceImpl
import com.prography.data.datasource.remote.PhotoRemoteDataSource
import com.prography.data.repository.ScreenshotRepositoryImpl
import com.prography.database.dao.ScreenshotDao
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.ScreenshotRepository
import com.prography.network.api.PhotoService
import com.prography.data.datasource.remote.PhotoRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun provideScreenshotLocalDataSource(
        dao: ScreenshotDao
    ): ScreenshotLocalDataSource {
        return ScreenshotLocalDataSourceImpl(dao)
    }

    @Provides
    fun provideScreenshotRepository(
        remoteDataSource: PhotoRemoteDataSource,
        localDataSource: ScreenshotLocalDataSource,
        userPrefs: UserPreferenceDataStore
    ): ScreenshotRepository {
        return ScreenshotRepositoryImpl(remoteDataSource, localDataSource, userPrefs)
    }

    @Singleton
    @Provides
    fun providePhotoRemoteDataSourceImpl(photoService: PhotoService
    ): PhotoRemoteDataSource = PhotoRemoteDataSourceImpl(photoService)
}
