package com.prography.data.di

import com.prography.data.datasource.local.ScreenshotLocalDataSource
import com.prography.data.datasource.local.ScreenshotLocalDataSourceImpl
import com.prography.data.local.repository.UserPreferenceRepositoryImpl
import com.prography.database.dao.ScreenshotDao
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.domain.repository.UserPreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(
        userPreferenceDataStore: UserPreferenceDataStore
    ): UserPreferenceRepository = UserPreferenceRepositoryImpl(userPreferenceDataStore)

    @Provides
    fun provideLocalDataSource(
        dao: ScreenshotDao
    ): ScreenshotLocalDataSource {
        return ScreenshotLocalDataSourceImpl(dao)
    }
}
