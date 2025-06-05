package com.prography.database.di

import com.prography.database.datasource.PhotoLocalDataSource
import com.prography.database.dao.BookmarkPhotoDao
import com.prography.database.datasource.PhotoLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun providePhotoDataSource(
        bookmarkPhotoDao: BookmarkPhotoDao
    ): PhotoLocalDataSource {
        return PhotoLocalDataSourceImpl(bookmarkPhotoDao)
    }
}