package com.prography.data.di

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.repository.ScreenshotRepositoryImpl
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.ScreenshotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun provideScreenshotRepository(
        localDataSource: ScreenshotLocalDataSource,
        userPrefs: UserPreferenceDataStore
    ): ScreenshotRepository {
        return ScreenshotRepositoryImpl(localDataSource, userPrefs)
    }
}
